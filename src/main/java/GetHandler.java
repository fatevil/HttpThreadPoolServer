import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * Created by marek on 21.5.16.
 */
public class GetHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
        String responseDataType = t.getRequestHeaders().getFirst("Accept");
        System.out.println(responseDataType);

        if (responseDataType.equals("*/*") || responseDataType.contains("text")) {
            String filename = t.getRequestURI().toString();
            if (filename.equals("/")) {
                filename = "/index.html";
            }

            File outputFile = new File(Server.CONTENT_DIR, filename);

            if (!outputFile.exists()) {
                String response = "It doesn't exist!";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }


            String response = FileUtils.readFileToString(outputFile);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            String filename = t.getRequestURI().toString();
            File outputFile = new File(Server.FILES_DIR, filename);

            if (!outputFile.exists()) {
                String response = "It doesn't exist!";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }


            // add the required response header for a PDF file
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "application/x-www-form-urlencoded");

            byte[] bytearray = new byte[(int) outputFile.length()];
            FileInputStream fis = new FileInputStream(outputFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytearray, 0, bytearray.length);

            // ok, we are ready to send the response.
            t.sendResponseHeaders(200, outputFile.length());
            OutputStream os = t.getResponseBody();
            os.write(bytearray, 0, bytearray.length);
            os.close();


        }
    }
}
