package com.oliver.myBasicHTTPServer;

public class Statistician {

    private static Statistician instance = new Statistician();

    private static int served = 0;
    private static int beingServed = 0;
    private static long servedTime = 0l;

    public static Statistician getInstance() {

        return instance;
    }

    private Statistician() {

    }

    public static synchronized void endRequest(long elapsedTime) {

        served++;
        servedTime += elapsedTime;
        beingServed--;
        System.out.println("\nRequest response time (ms): " + elapsedTime / 1000000);
        System.out.println("Requests served: " + served);
        long avg = 0l;
        if(served > 0) avg = servedTime / served;
        System.out.println("Average request response time (ms): " + avg / 1000000);
        System.out.println("Requests in process: " + beingServed);
    }

    public static synchronized void startRequest() {

        beingServed++;
        System.out.println("\nRequests in process: " + beingServed);
    }
}