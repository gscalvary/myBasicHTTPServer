package com.oliver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class SimpleServletRequest {

    // Holds request data broken down into their name value pairs.
    private HashMap<String, String> request;

    public SimpleServletRequest(InputStream inputStream) {

        request = new HashMap<>();

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            /* Parse input. */
            String input = bufferedReader.readLine();
            boolean parseRequestLine = true;
            while(input != null) {
                System.out.println(input);
                if(parseRequestLine) {
                    parseRequest(input);
                    parseRequestLine = false;
                } else {
                    parsePair(input);
                }
                input = bufferedReader.readLine();
            }
        } catch(IOException ioe) {
            System.err.println("I/O error with socket " + Configuration.PORT + ": " + ioe);
        }
    }

    /**
     * Gets the Host parameter as supplied by the client
     *
     * @return The host
     */
    public String getHost() {

        return request.get("Host");
    }

    /**
     * Gets the entire url path as supplied by the client, including request parameters
     *
     * @return The host
     */
    public String getUrlPath() {

        return request.get("UrlPath");
    }

    /**
     * Gets a specific parameter as parsed from request url
     *
     * @param name The parameter name
     * @return The parameter value
     */
    public String getUrlParameter(String name) {

        return request.get(name);
    }

    /**
     * Gets a request header as supplied by the client
     *
     * @param name The header name
     * @return The header value
     */
    public String getHeader(String name) {

        return request.get(name);
    }

    /**
     * Gets the client's network address
     *
     * @return The clients network address
     */
    public java.net.InetAddress getClientAddress() {

        if(request.get("Remote_Addr") != null) {
            try {
                return InetAddress.getByName(request.get("Remote_Addr"));
            } catch(UnknownHostException uhe) {
                return null;
            }
        } else if(request.get("HTTP_X_FORWARDED_FOR") != null) {
            try {
                return InetAddress.getByName(request.get("HTTP_X_FORWARDED_FOR"));
            } catch (UnknownHostException uhe) {
                return null;
            }
        }

        return null;
    }

    private void parseRequest(String input) {

        if(input == null || input.isEmpty()) return;

        String[] terms = input.split(" ");
        request.put("Type", terms[0].toUpperCase());
        if(terms[1].endsWith("/")) terms[1] += Configuration.DEFAULT_FILE;
        request.put("UrlPath", terms[1]);

        parseURL(terms[1]);
    }

    private void parseURL(String input) {

        if(input == null || input.isEmpty()) return;

        String[] terms = input.split("/?");

        parseURLParameters(terms[1]);
    }

    private void parseURLParameters(String input) {

        if(input == null || input.isEmpty()) return;

        String[] terms = input.split("&");
        for(String term : terms) {
            String[] pair = term.split("=");
            request.put(pair[0], pair[1]);
        }
    }

    private void parsePair(String input) {

        if(input == null || input.isEmpty()) return;

        for(int i = 0; i < input.length(); i++) {
            if(input.charAt(i) == ':') {
                if(i + 2 < input.length() && i - 1 >= 0) {
                    request.put(input.substring(0, i - 1), input.substring(i + 2));
                }
                break;
            }
        }
    }
}
