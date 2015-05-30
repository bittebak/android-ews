package com.yellowtwig.ewscalandar;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import java.io.StringWriter;

import ews.FindItemRequest;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
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
}