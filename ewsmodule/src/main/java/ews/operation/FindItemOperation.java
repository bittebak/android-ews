package ews.operation;

import java.io.IOException;
import java.io.InputStream;

import ews.message.EwsRequest;
import ews.message.FindItemRequest;
import ews.message.FindItemResponse;
import ews.transport.RequestHandler;

/**
 * Created by marcprive on 05-31-15.
 */
public class FindItemOperation {
    private RequestHandler handler;

    public FindItemOperation(RequestHandler handler) {
        this.handler = handler;
    }

    public FindItemResponse execute() throws IOException {
        EwsRequest request = new FindItemRequest();
        InputStream result = handler.postRequest(request);

        FindItemResponse response = new FindItemResponse();
        response.parse(result);
        return response;

    }
}
