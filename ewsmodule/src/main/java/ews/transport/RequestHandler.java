package ews.transport;

import android.util.Log;

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import ews.http.EWSHttpException;
import ews.http.ExchangeCredentials;
import ews.http.ExchangeServiceBase;
import ews.http.HttpClientWebRequest;
import ews.http.HttpWebRequest;
import ews.http.ServiceLocalException;
import ews.http.WebCredentials;
import ews.message.EwsRequest;
import ews.message.FindItemRequest;
import ews.microsoft.ExchangeVersion;
import ews.ntlm.JCIFSNTLMSchemeFactory;
import jcifs.http.NtlmHttpURLConnection;
import jcifs.smb.NtlmAuthenticator;


/**
 * Created by marcprive on 05-30-15.
 */
public class RequestHandler {

    private static final String HOSTNAME = "https://webmail.stater.com/ews/Exchange.asmx";

    public RequestHandler() {

    }


    public void postRequest(EwsRequest request) throws IOException {
        String responseString = "";
        String outputString = "";
        String wsURL = "https://webmail.stater.com/ews/Exchange.asmx";
        URL url = new URL(wsURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection)connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        //From NetBeans project mavenproject1
        Authenticator.setDefault(new ExchangeNTLMAuthenticator("europe","sogeelenm","ZesKeer7"));

        OutputStreamWriter writer = new OutputStreamWriter(bout);
        request.write(writer);

        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                String.valueOf(bout.size()));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        //httpConn.setRequestProperty("SOAPAction", SOAPAction);
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();
        //Write the content of the request to the outputstream of the HTTP Connection.
        out.write(bout.toByteArray());
        out.close();
        //Ready with sending the request.
        //Read the response.
        InputStreamReader isr =
                new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);
        //Write the SOAP message response to a String.
        while ((responseString = in.readLine()) != null) {
            outputString = outputString + responseString;
        }

    }

}
