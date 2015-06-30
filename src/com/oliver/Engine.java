package com.oliver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Engine implements Runnable {

    private Socket socket;
    private static boolean shutdown = false;
    /* The high level directory of the files the server may serve. */
    public static File RESOURCE_DIRECTORY = null;
    /* The high level directory of the servlets the server may serve. */
    public static File SERVLET_DIRECTORY = null;

    public Engine(Socket socket) {

        this.socket = socket;
    }

    public static void main(String[] args) {

        /* Set the resource and servlet directories. */
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        RESOURCE_DIRECTORY = new File(s + Configuration.RESOURCES);
        SERVLET_DIRECTORY = new File(s + Configuration.SERVLETS);

        /* Establish a listener. */
        try {
            ServerSocket serverSocket = new ServerSocket(Configuration.PORT, Configuration.QUEUE_LENGTH);
            System.out.println("\nHTTP Server listening on port " + Configuration.PORT + " with a queue length" +
                    " of " + Configuration.QUEUE_LENGTH + ".");
            /* Listen until the user quits the application. */
            while(!shutdown) {
                /* The ServerSocket accept method is a blocking call that waits for a client initiated communication. */
                Engine engine = new Engine(serverSocket.accept());
                /* Run each connection in its own thread to facilitate concurrency. */
                Thread engineThread = new Thread(engine);
                engineThread.start();
            }
            System.out.println("\nServer shut down command received, server shut down.");
        } catch(IOException e) {
            System.err.println("Unable to connect to socket " + Configuration.PORT + ": " + e);
        }
    }

    @Override
    public void run() {

        Statistician.startRequest();
        long startTime = System.nanoTime();

        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()) {

            ServletRequest request = new ServletRequest(inputStream);
            ServletResponse response = new ServletResponse(outputStream, request);

            if (request.getHeader("Type") != null && request.getHeader("Type").equals("GET")) {
                System.out.println("\nServing " + request.getUrlPath());
                /* Shut down the server if the shut down command was received. */
                if(request.getUrlPath().equals(Configuration.SHUTDOWN_COMMAND)) {
                    shutdown = true;
                    response.setStatusCode(200);
                    response.sendMessage();
                /* Process servlets. */
                } else if(request.getUrlPath().toLowerCase().startsWith(Configuration.SERVLET)) {
                    ServletProcessor servletProcessor = new ServletProcessor();
                    servletProcessor.process(request, response);
                } else {
                /* Send a static resource to the client. */
                    response.sendResource();
                }
            } else {
                System.out.println("Request of type " + request.getHeader("Type") + " rejected.");
                response.setStatusCode(501);
                response.sendMessage();
            }

        } catch(IOException ioe) {
            System.err.println("I/O error with socket " + Configuration.PORT + ": " + ioe);
        }

        long endTime = System.nanoTime();
        Statistician.endRequest(endTime - startTime);
    }
}