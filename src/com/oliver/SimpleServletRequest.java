package com.oliver;

/**
 * This interface encapsulates the HTTP request portion
 * of the SimpleServlet specification.
 */
public interface SimpleServletRequest {

    /**
     * Gets the Host parameter as supplied by the client
     *
     * @return The host
     */
    public String getHost();

    /**
     * Gets the entire url path as supplied by the client, including request parameters
     *
     * @return The host
     */
    public String getUrlPath();

    /**
     * Gets a specific parameter as parsed from request url
     *
     * @param name The parameter name
     * @return The parameter value
     */
    public String getUrlParameter(String name);

    /**
     * Gets a request header as supplied by the client
     *
     * @param name The header name
     * @return The header value
     */
    public String getHeader(String name);

    /**
     * Gets the client's network address
     *
     * @return The clients network address
     */
    public java.net.InetAddress getClientAddress();
}
