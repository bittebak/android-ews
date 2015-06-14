package ews.transport;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.Authenticator;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLConnection;

import ews.config.ConnectionConfig;
import ews.message.EwsRequest;

/**
 * Created by marcprive on 05-30-15.
 */
public class RequestHandler {


    private ConnectionConfig config;

    public RequestHandler( ConnectionConfig config) {

        this.config = config;
    }


    public InputStream postRequest(EwsRequest request) throws IOException {
        String responseString = "";
        String outputString = "";

        String wsURL = config.getServiceURL();
        URL url = new URL(wsURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection)connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        String domain = config.getDomain();
        String userName = config.getUserName();
        String password = config.getPassword();

        Authenticator.setDefault(new ExchangeNTLMAuthenticator(
                domain ,
                userName,
                password));


        OutputStreamWriter writer = new OutputStreamWriter(bout);
        request.write(writer);

        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                String.valueOf(bout.size()));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();

        //Write the content of the request to the outputstream of the HTTP Connection.
        out.write(bout.toByteArray());
        out.close();
        InputStream input = null;
        try {
            input = httpConn.getInputStream();
        }
        catch (Exception ex){
            ex.printStackTrace();

        }

        return input;
    }

}
