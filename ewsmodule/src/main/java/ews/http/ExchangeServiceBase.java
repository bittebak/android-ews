/*
 * The MIT License
 * Copyright (c) 2012 Microsoft Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ews.http;

import android.util.Log;

import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import ews.microsoft.ExchangeVersion;

/**
 * Represents an abstract binding to an Exchange Service.
 */
public  class ExchangeServiceBase implements Closeable {
  /**
   * The credential.
   */
  private ExchangeCredentials credentials;

  /**
   * The use default credential.
   */
  private boolean useDefaultCredentials;

  /**
   * The binary secret.
   */
  private static byte[] binarySecret;

  /**
   * The timeout.
   */
  private int timeout = 100000;

  /**
   * The trace enabled.
   */
  private boolean traceEnabled;



  /**
   * The pre authenticate.
   */
  private boolean preAuthenticate;

  /**
   * The user agent.
   */
  private String userAgent = ExchangeServiceBase.defaultUserAgent;

  /**
   * The accept gzip encoding.
   */
  private boolean acceptGzipEncoding = true;

  /**
   * The requested server version.
   */
  private ExchangeVersion requestedServerVersion = ExchangeVersion.Exchange2010_SP2;



  private Map<String, String> httpHeaders = new HashMap<String, String>();

  private Map<String, String> httpResponseHeaders = new HashMap<String, String>();


  protected CloseableHttpClient httpClient;

  protected HttpClientContext httpContext;

  protected HttpClientWebRequest request = null;

  // protected static HttpStatusCode AccountIsLocked = (HttpStatusCode)456;

  /**
   * Default UserAgent.
   */
  private static String defaultUserAgent = "ExchangeServicesClient/20150530" ;

  /**
   * Initializes a new instance.
   *
   * This constructor performs the initialization of the HTTP connection manager, so it should be called by
   * every other constructor.
   */
  protected ExchangeServiceBase() {
    setUseDefaultCredentials(true);
    initializeHttpClient();
    initializeHttpContext();
  }

  public ExchangeServiceBase(ExchangeVersion requestedServerVersion) {
    this();
    this.requestedServerVersion = requestedServerVersion;
  }

  protected ExchangeServiceBase(ExchangeServiceBase service, ExchangeVersion requestedServerVersion) {
    this(requestedServerVersion);
    this.useDefaultCredentials = service.getUseDefaultCredentials();
    this.credentials = service.getCredentials();
    this.timeout = service.getTimeout();
    this.preAuthenticate = service.isPreAuthenticate();
    this.userAgent = service.getUserAgent();
    this.acceptGzipEncoding = service.getAcceptGzipEncoding();
    this.httpHeaders = service.getHttpHeaders();
  }

  private void initializeHttpClient() {
    Registry<ConnectionSocketFactory> registry = createConnectionSocketFactoryRegistry();
    HttpClientConnectionManager httpConnectionManager = new BasicHttpClientConnectionManager(registry);
    AuthenticationStrategy authStrategy = new CookieProcessingTargetAuthenticationStrategy();

    httpClient = HttpClients.custom()
      .setConnectionManager(httpConnectionManager)
      .setTargetAuthenticationStrategy(authStrategy)
      .build();
  }

  /**
   * Create registry with configured {@link ConnectionSocketFactory} instances.
   * Override this method to change how to work with different schemas.
   *
   * @return registry object
   */
  protected Registry<ConnectionSocketFactory> createConnectionSocketFactoryRegistry() {
    try {
      return RegistryBuilder.<ConnectionSocketFactory>create()
        .register(EWSConstants.HTTP_SCHEME, new PlainConnectionSocketFactory())
        .register(EWSConstants.HTTPS_SCHEME, EwsSSLProtocolSocketFactory.build(null))
        .build();
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(
        "Could not initialize ConnectionSocketFactory instances for HttpClientConnectionManager", e
      );
    }
  }

  /**
   * (Re)initializes the HttpContext object. This removes any existing state (mainly cookies). Use an own
   * cookie store, instead of the httpClient's global store, so cookies get reset on reinitialization
   */
  private void initializeHttpContext() {
    CookieStore cookieStore = new BasicCookieStore();
    httpContext = HttpClientContext.create();
    httpContext.setCookieStore(cookieStore);
  }

  @Override
  public void close() {
    try {
      httpClient.close();
    } catch (IOException e) {
      // Ignore exception while closing the HttpClient.
    }
  }

  // Event handlers



  // Utilities

  /**
   * Creates an HttpWebRequest instance and initialises it with the
   * appropriate parameters, based on the configuration of this service
   * object.
   *
   * @param url                The URL that the HttpWebRequest should target.
   * @param acceptGzipEncoding If true, ask server for GZip compressed content.
   * @param allowAutoRedirect  If true, redirection response will be automatically followed.
   * @return An initialised instance of HttpWebRequest.
   * @throws ServiceLocalException       the service local exception
   * @throws URISyntaxException the uRI syntax exception
   */
  public HttpWebRequest prepareHttpWebRequestForUrl(URI url, boolean acceptGzipEncoding,
      boolean allowAutoRedirect) throws ServiceLocalException, URISyntaxException {
    // Verify that the protocol is something that we can handle
    String scheme = url.getScheme();
    if (!scheme.equalsIgnoreCase(EWSConstants.HTTP_SCHEME)
      && !scheme.equalsIgnoreCase(EWSConstants.HTTPS_SCHEME)) {
      String strErr = String.format("Protocol %s isn't supported for service request.", scheme);
      throw new ServiceLocalException(strErr);
    }

    request = new HttpClientWebRequest(httpClient, httpContext);
    try {
      request.setUrl(url.toURL());
    } catch (MalformedURLException e) {
      String strErr = String.format("Incorrect format : %s", url);
      throw new ServiceLocalException(strErr);
    }

    request.setPreAuthenticate(preAuthenticate);
    request.setTimeout(timeout);
    request.setContentType("text/xml; charset=utf-8");
    request.setAccept("text/xml");
    request.setUserAgent(userAgent);
    request.setAllowAutoRedirect(allowAutoRedirect);
    request.setAcceptGzipEncoding(acceptGzipEncoding);
    request.setHeaders(getHttpHeaders());

    prepareCredentials(request);

    request.prepareConnection();

    httpResponseHeaders.clear();

    return request;
  }

  protected void prepareCredentials(HttpWebRequest request) throws ServiceLocalException, URISyntaxException {
    request.setUseDefaultCredentials(useDefaultCredentials);
    if (!useDefaultCredentials) {
      if (credentials == null) {
        throw new ServiceLocalException("Credentials are required to make a service request.");
      }

      // Make sure that credential have been authenticated if required
      credentials.preAuthenticate();

      // Apply credential to the request
      credentials.prepareWebRequest(request);
    }
  }



  /**
   * @param location file path
   * @return false if location is null,true if this abstract pathname is absolute
   */
  public static boolean checkURIPath(String location) {
    if (location == null) {
      return false;
    }
    final File file = new File(location);
    return file.isAbsolute();
  }


  /**
   * Converts the date time to universal date time string.
   *
   * @param dt the date
   * @return String representation of DateTime in yyyy-MM-ddTHH:mm:ssZ format.
   */
  public String convertDateTimeToUniversalDateTimeString(Date dt) {
    String utcPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    DateFormat utcFormatter = new SimpleDateFormat(utcPattern);
    utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    return utcFormatter.format(dt);
  }

  /**
   * Sets the user agent to a custom value
   *
   * @param userAgent User agent string to set on the service
   */
  protected void setCustomUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  /**
   * Validates this instance.
   *
   * @throws ServiceLocalException the service local exception
   */
  public void validate() throws ServiceLocalException {
  }




  /**
   * Gets the credential used to authenticate with the Exchange Web Services.
   *
   * @return credential
   */
  public ExchangeCredentials getCredentials() {
    return this.credentials;
  }

  /**
   * Sets the credential used to authenticate with the Exchange Web Services.
   * Setting the Credentials property automatically sets the
   * UseDefaultCredentials to false.
   *
   * @param credentials Exchange credential.
   */
  public void setCredentials(ExchangeCredentials credentials) {
    this.credentials = credentials;
    this.useDefaultCredentials = false;

    // Reset the httpContext, to remove any existing authentication cookies from subsequent request
    initializeHttpContext();
  }

  /**
   * Gets a value indicating whether the credential of the user currently
   * logged into Windows should be used to authenticate with the Exchange Web
   * Services.
   *
   * @return true if credential of the user currently logged in are used
   */
  public boolean getUseDefaultCredentials() {
    return this.useDefaultCredentials;
  }

  /**
   * Sets a value indicating whether the credential of the user currently
   * logged into Windows should be used to authenticate with the Exchange Web
   * Services. Setting UseDefaultCredentials to true automatically sets the
   * Credentials property to null.
   *
   * @param value the new use default credential
   */
  public void setUseDefaultCredentials(boolean value) {
    this.useDefaultCredentials = value;
    if (value) {
      this.credentials = null;
    }

    // Reset the httpContext, to remove any existing authentication cookies from subsequent request
    initializeHttpContext();
  }
  
  /**
   * Gets the timeout used when sending HTTP request and when receiving HTTP
   * response, in milliseconds.
   *
   * @return timeout in milliseconds
   */
  public int getTimeout() {
    return timeout;
  }

  /**
   * Sets the timeout used when sending HTTP request and when receiving HTTP
   * respones, in milliseconds. Defaults to 100000.
   *
   * @param timeout timeout in milliseconds
   */
  public void setTimeout(int timeout) {
    if (timeout < 1) {
      throw new IllegalArgumentException("Timeout must be greater than zero.");
    }
    this.timeout = timeout;
  }

  /**
   * Gets a value that indicates whether HTTP pre-authentication should be
   * performed.
   *
   * @return true indicates pre-authentication is set
   */
  public boolean isPreAuthenticate() {
    return preAuthenticate;
  }

  /**
   * Sets a value that indicates whether HTTP pre-authentication should be
   * performed.
   *
   * @param preAuthenticate true to enable pre-authentication
   */
  public void setPreAuthenticate(boolean preAuthenticate) {
    this.preAuthenticate = preAuthenticate;
  }

  /**
   * Gets a value indicating whether GZip compression encoding should be
   * accepted. This value will tell the server that the client is able to
   * handle GZip compression encoding. The server will only send Gzip
   * compressed content if it has been configured to do so.
   *
   * @return true if compression is used
   */
  public boolean getAcceptGzipEncoding() {
    return acceptGzipEncoding;
  }

  /**
   * Gets a value indicating whether GZip compression encoding should
   * be accepted. This value will tell the server that the client is able to
   * handle GZip compression encoding. The server will only send Gzip
   * compressed content if it has been configured to do so.
   *
   * @param acceptGzipEncoding true to enable compression
   */
  public void setAcceptGzipEncoding(boolean acceptGzipEncoding) {
    this.acceptGzipEncoding = acceptGzipEncoding;
  }

  /**
   * Gets the requested server version.
   *
   * @return The requested server version.
   */
  public ExchangeVersion getRequestedServerVersion() {
    return this.requestedServerVersion;
  }

  /**
   * Gets the user agent.
   *
   * @return The user agent.
   */
  public String getUserAgent() {
    return this.userAgent;
  }

  /**
   * Sets the user agent.
   *
   * @param userAgent The user agent
   */
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent + " (" + ExchangeServiceBase.defaultUserAgent + ")";
  }



  /**
   * Gets a collection of HTTP headers that will be sent with each request to
   * EWS.
   *
   * @return httpHeaders
   */
  public Map<String, String> getHttpHeaders() {
    return this.httpHeaders;
  }

  // Events


  /**
   * Save the HTTP response headers.
   *
   * @param headers The response headers
   */
  private void saveHttpResponseHeaders(Map<String, String> headers) {
    this.httpResponseHeaders.clear();

    for (String key : headers.keySet()) {
      this.httpResponseHeaders.put(key, headers.get(key));
    }
  }

  /**
   * Gets a collection of HTTP headers from the last response.
   * @return HTTP response headers
   */
  public Map<String, String> getHttpResponseHeaders() {
    return this.httpResponseHeaders;
  }

  /**
   * Gets the session key.
   * @return session key
   */
  public static byte[] getSessionKey() {
    // this has to be computed only once.
    synchronized (ExchangeServiceBase.class) {
      if (ExchangeServiceBase.binarySecret == null) {
        Random randomNumberGenerator = new Random();
        ExchangeServiceBase.binarySecret = new byte[256 / 8];
        randomNumberGenerator.nextBytes(binarySecret);
      }

      return ExchangeServiceBase.binarySecret;
    }
  }

  public void setUrl(URI uri) {
  }
}
