package com.oliver;

public class Configuration {

    /* The port on which the server listens. */
    public static final int PORT = 8080;
    /* The number of connection requests the server will queue before refusing connections. */
    public static final int QUEUE_LENGTH = 50;
    /* The default location of resources served by this server. */
    public static final String RESOURCES = "/resources";
    /* The default location of servlets run by this server. */
    public static final String SERVLETS = "/resources/servlets";
    /* The default file the server will serve if an HTTP request does not specify a file. */
    public static final String DEFAULT_FILE = "myportfolio.html";
    /* The URI request string that specifies a servlet. */
    public static final String SERVLET = "/servlets/";
    /* The command that shuts down the server. */
    public static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
}
