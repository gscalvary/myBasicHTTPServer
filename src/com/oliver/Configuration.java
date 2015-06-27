package com.oliver;

import java.io.File;

public class Configuration {

    /* The port on which the server listens. */
    public static final int PORT = 8080;
    /* The number of connection requests the server will queue before refusing connections. */
    public static final int QUEUE_LENGTH = 50;
    /* The high level directory of the files the server may serve. */
    public static final File RESOURCE_DIRECTORY = new File("/Users/Christopher/Documents/Web Projects/Portfolio-Site");
    /* The default file the server will serve if an HTTP request does not specify a file. */
    public static final String DEFAULT_FILE = "myportfolio.html";
    /* The command that shuts down the server. */
    public static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
}
