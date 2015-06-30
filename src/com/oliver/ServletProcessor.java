package com.oliver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class ServletProcessor {

    public void process(ServletRequest request, ServletResponse response) {

        /* Suss out the servlet name from the URL */
        String url = request.getUrlPath();
        String servletName = url.substring(url.lastIndexOf("/") + 1);

        /* Build a class loader. */
        URLClassLoader loader = null;
        try {
            URL[] urls = new URL[1];
            // Used to get around a URL constructor error that would otherwise rear its ugly head below.
            URLStreamHandler urlStreamHandler = null;
            String servletDirectory =
                    (new URL("file", null, Engine.SERVLET_DIRECTORY.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, servletDirectory, urlStreamHandler);
            loader = new URLClassLoader(urls);
        } catch(IOException ioe) {
            System.out.println("Servlet processor unable to retrieve servlet directory path: " + ioe);
            response.setStatusCode(500);
            response.sendMessage();
            return;
        }

        /* Build the servlet class. */
        Class servletClass = null;
        try {
            servletClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Servlet processor unable to load class: " + cnfe);
            response.setStatusCode(500);
            response.sendMessage();
            return;
        }

        /* Build and execute the simple servlet. */
        try {
            SimpleServlet simpleServlet = (SimpleServlet) servletClass.newInstance();
            simpleServlet.doGet(request, response);
        } catch(Exception e) {
            System.out.println("Servlet processor unable to instantiate servlet class: " + e);
            response.setStatusCode(500);
            response.sendMessage();
        }
    }
}
