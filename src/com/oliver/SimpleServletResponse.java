package com.oliver;

import java.io.*;
import java.util.Date;
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

        File file = new File(Configuration.RESOURCE_DIRECTORY, request.getUrlPath().toLowerCase());
        byte[] dataToServe = new byte[(int) file.length()];

        try(FileInputStream fileInputStream = new FileInputStream(file);
            PrintWriter printWriter = new PrintWriter(outputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            int bytes = fileInputStream.read(dataToServe);
            setMimeType(getMIMEType(request.getUrlPath()));
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Server: Basic HTTP Server 1.0");
            printWriter.println("Date: " + new Date());
            printWriter.println("Content-type: " + response.get("mimeType"));
            printWriter.println("Content-length: " + (int) file.length());
            printWriter.println();
            printWriter.flush();
            if(bytes > 0) {
                bufferedOutputStream.write(dataToServe, 0, (int) file.length());
                bufferedOutputStream.flush();
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Resource, " + request.getUrlPath().toLowerCase() + " , not found: " + fnfe);
            setStatusCode(404);
            sendMessage();
        } catch(IOException ioe) {
            System.err.println("I/O error with file read " + request.getUrlPath().toLowerCase() + ": " + ioe);
        }
    }

    /**
     * Send a basic message back to the client, used primarily for error responses or acknowledgements of server
     * commands.
     *
     */

    public void sendMessage() {

        String header, title, body;

        switch(response.get("httpStatusCode")) {
            case "200":
                header = "HTTP/1.1 200 OK";
                title = "OK";
                body = "Basic HTTP Server: "  + request.getUrlPath();
                break;
            case "404":
                header = "HTTP/1.1 404 File Not Found";
                title = "Not Implemented";
                body = "404 File Not Found: " + request.getUrlPath();
                break;
            case "501":
                header = "HTTP/1.1 501 Not Implemented";
                title = "File Not Found";
                body = "501 Not Implemented: "  + request.getHeader("Type");
                break;
            default:
                System.err.println("Attempt to return HTTP " + response.get("httpStatusCode") +
                        " which is not implemented.");
                return;
        }

        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(header);
        printWriter.println("Server: Basic HTTP Server 1.0");
        printWriter.println("Date: " + new Date());
        printWriter.println("Content-type: " + response.get("mimeType"));
        printWriter.println();
        printWriter.println("<HTML>");
        printWriter.println("<HEAD><TITLE>" + title + "</TITLE>");
        printWriter.println("<BODY>");
        printWriter.println("<H2>" + body + "</H2>");
        printWriter.println("</BODY></HTML>");
        printWriter.flush();
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

    /**
     * Gets the MIME type of the static resource being returned.
     *
     * @return the MIME type.
     */
    private String getMIMEType(String resource) {

        if(resource.endsWith(".html")) {
            return "text/html";
        } else if(resource.endsWith(".css")) {
            return "text/css";
        } else if(resource.endsWith(".js")) {
            return "application/javascript";
        } else if(resource.endsWith(".json")) {
            return "application/json";
        } else if(resource.endsWith(".jpg")) {
            return "image/jpeg";
        } else return "text/plain";
    }
}
