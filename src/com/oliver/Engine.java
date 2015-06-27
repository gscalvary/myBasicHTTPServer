package com.oliver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

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

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream())) {
            /* Parse input. */
            String input = bufferedReader.readLine();
            if(input != null) {
                String[] request = input.split(" ");
                /* Handle input verbs. */
                if (request[0].toUpperCase().equals("GET")) {
                    System.out.println("\nServing " + request[1]);
                    /* Serve up the default file if none was specified in the HTTP request. */
                    if(request[1].endsWith("/")) request[1] += Configuration.DEFAULT_FILE;
                    /* Shut down the server if the shut down command was received. */
                    if(request[1].equals(Configuration.SHUTDOWN_COMMAND)) {
                        shutdown = true;
                        sendMessage(200, printWriter, request[1]);
                    } else {
                        /* Get the resource and return it. */
                        File file = new File(Configuration.RESOURCE_DIRECTORY, request[1].toLowerCase());
                        byte[] dataToServe = new byte[(int) file.length()];
                        try (FileInputStream fileInputStream = new FileInputStream(file)) {
                            int bytes = fileInputStream.read(dataToServe);
                            printWriter.println("HTTP/1.1 200 OK");
                            printWriter.println("Server: Basic HTTP Server 1.0");
                            printWriter.println("Date: " + new Date());
                            printWriter.println("Content-type: " + getContentType(request[1]));
                            printWriter.println("Content-length: " + (int) file.length());
                            printWriter.println();
                            printWriter.flush();
                            if(bytes > 0) {
                                bufferedOutputStream.write(dataToServe, 0, (int) file.length());
                                bufferedOutputStream.flush();
                            }
                        } catch (FileNotFoundException fnfe) {
                            System.out.println("Resource not found: " + fnfe);
                            sendMessage(404, printWriter, request[1]);
                        }
                    }
                } else {
                    /* Only GET is implemented. */
                    System.out.println("Request of type " + request[0] + " rejected.");
                    sendMessage(501, printWriter, request[0]);
                }
            }
        } catch(IOException ioe) {
            System.err.println("I/O error with socket " + Configuration.PORT + ": " + ioe);
        } finally {
            try {
                socket.close();
            } catch(Exception e) {
                System.err.println("Unable to close socket " + Configuration.PORT + " connection: " + e);
            }
        }

        long endTime = System.nanoTime();
        Statistician.endRequest(endTime - startTime);
    }

    private void sendMessage(int type, PrintWriter printWriter, String request) {

        String header, title, body;

        switch(type) {
            case 200:
                header = "HTTP/1.1 200 OK";
                title = "OK";
                body = "Basic HTTP Server: ";
                break;
            case 404:
                header = "HTTP/1.1 404 File Not Found";
                title = "Not Implemented";
                body = "404 File Not Found: ";
                break;
            case 501:
                header = "HTTP/1.1 501 Not Implemented";
                title = "File Not Found";
                body = "501 Not Implemented: ";
                break;
            default:
                System.err.println("Attempt to return HTTP " + type + " which is not implemented.");
                return;
        }

        printWriter.println(header);
        printWriter.println("Server: Basic HTTP Server 1.0");
        printWriter.println("Date: " + new Date());
        printWriter.println("Content-type: text/html");
        printWriter.println();
        printWriter.println("<HTML>");
        printWriter.println("<HEAD><TITLE>" + title + "</TITLE>");
        printWriter.println("<BODY>");
        printWriter.println("<H2>" + body + request + "</H2>");
        printWriter.println("</BODY></HTML>");
        printWriter.flush();
    }

    private String getContentType(String resource) {

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
