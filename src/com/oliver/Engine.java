package com.oliver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Engine implements Runnable {

    private Socket socket;
    private static boolean shutdown = false;

    public Engine(Socket socket) {

        this.socket = socket;
    }

    public static void main(String[] args) {

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

            SimpleServletRequest request = new SimpleServletRequest(inputStream);
            SimpleServletResponse response = new SimpleServletResponse(outputStream, request);

            if (request.getHeader("Type") != null && request.getHeader("Type").equals("GET")) {
                System.out.println("\nServing " + request.getUrlPath());
                /* Shut down the server if the shut down command was received. */
                if (request.getUrlPath().equals(Configuration.SHUTDOWN_COMMAND)) {
                    shutdown = true;
                    response.setStatusCode(200);
                    response.sendMessage();
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
