package com.oliver;

import java.io.OutputStream;
import java.util.HashMap;

public class SimpleServletResponse {

    private OutputStream outputStream;
    private SimpleServletRequest request;

    // Holds response data broken down into their name value pairs.
    private HashMap<String, String> response;

    public SimpleServletResponse(OutputStream outputStream, SimpleServletRequest request) {

        this.outputStream = outputStream;
        this.request = request;
        response = new HashMap<>();
        response.put("httpStatusCode", "501");
        response.put("mimeType", "text/html");
    }

    public void sendResource() {


    }

    /**
     * Set the response HTTP code
     *
     * @param httpStatusCode The status code for the response.
     */
    public void setStatusCode(int httpStatusCode) {

        response.replace("httpStatusCode", Integer.toString(httpStatusCode));
    }

    /**
     * Sets the response header to specified value
     *
     * @param name The parameter name
     * @param value The parameter value
     */
    public void setHeader(String name, String value) {

        if(response.containsKey(name)) {
            response.replace(name, value);
        } else {
            response.put(name, value);
        }
    }

    /**
     * Sets the mime type to be returned to the client
     *
     * @param mimeType The mime type to set
     */
    public void setMimeType(String mimeType) {

        response.replace("mimeType", mimeType);
    }

    /**
     * Gets the OutputStream that when written to will transmit to the client.
     *
     * @return The output stream
     */
    public OutputStream getOutputStream() {

        return outputStream;
    }
}
