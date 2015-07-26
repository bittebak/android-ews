package com.yellowtwig.ewscalandar;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import java.io.IOException;
import java.io.StringWriter;

import ews.config.ConnectionConfig;
import ews.message.FindItemRequest;
import ews.message.FindItemResponse;
import ews.operation.FindItemOperation;
import ews.transport.RequestHandler;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private static final String HOSTNAME = "https://yourhost/ews/Exchange.asmx";
    public ApplicationTest() {
        super(Application.class);
    }

    @SmallTest
    public void testPreconditions() {
        StringWriter writer = new StringWriter();

        writer.write("test");
        Log.d("test", writer.getBuffer().toString());
    }

    @SmallTest
    public void testMakeRequest() {
        FindItemRequest r = new FindItemRequest();

        StringWriter writer = new StringWriter();

        r.write(writer);
        String request = writer.getBuffer().toString();
        Log.d(request, writer.getBuffer().toString());
    }

    @SmallTest
    public void testGetFolder(){

        RequestHandler handler = new RequestHandler(loadPreferences());
        FindItemRequest request = new FindItemRequest();


        try {
            handler.postRequest(request);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SmallTest
    public void testGetFolderResponse(){

        RequestHandler handler = new RequestHandler(loadPreferences());
        FindItemOperation operation = new FindItemOperation(handler);

        try {
            FindItemResponse response = operation.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ConnectionConfig loadPreferences(){
        ConnectionConfig config = new ConnectionConfig();

        config.setDomain("domain");
        config.setUserName("username");
        config.setPassword("password");
        config.setServiceURL(HOSTNAME);
        return config;
    }
}