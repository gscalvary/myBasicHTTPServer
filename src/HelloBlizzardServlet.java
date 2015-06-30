import com.oliver.SimpleServlet;
import com.oliver.SimpleServletRequest;
import com.oliver.SimpleServletResponse;

import java.io.PrintWriter;
import java.util.Date;

public class HelloBlizzardServlet implements SimpleServlet {

    public void doGet(SimpleServletRequest request, SimpleServletResponse response) {

        PrintWriter printWriter = new PrintWriter(response.getOutputStream());
        printWriter.println("HTTP/1.1 200 OK");
        printWriter.println("Server: Basic HTTP Server 1.0");
        printWriter.println("Date: " + new Date());
        printWriter.println("Content-type: text/html");
        printWriter.println();
        printWriter.println("<HTML>");
        printWriter.println("<HEAD><TITLE>OK</TITLE>");
        printWriter.println("<BODY>");
        printWriter.println("<H2>Hello Blizzard Servlet exclaims: HELLO BLIZZARD!</H2>");
        printWriter.println("</BODY></HTML>");
        printWriter.flush();
    }
}
