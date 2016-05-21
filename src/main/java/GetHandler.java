import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by marek on 21.5.16.
 */
public class GetHandler extends AbstractHttpHandler {

    @Override
    public void handle(HttpExchange t) {
        String responseDataType = t.getRequestHeaders().getFirst("Accept");

        if (responseDataType.equals("*/*") || responseDataType.contains("text")) {
            giveTextReponse(t);
        } else {
            giveFileReponse(t);
        }
    }

    private void giveFileReponse(HttpExchange t) {
        String filename = t.getRequestURI().toString();
        File outputFile = new File(Server.FILES_DIR, filename);

        if (!outputFile.exists())
            sendResponseAndClose(404, "File doesn't exist!", t);


        // add the required response header for a PDF file
        Headers h = t.getResponseHeaders();
        h.add("Content-Type", "application/x-www-form-urlencoded");

        byte[] bytearray = new byte[(int) outputFile.length()];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(outputFile);
            bis = new BufferedInputStream(fis);
            bis.read(bytearray, 0, bytearray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendDataAndClose(200, bytearray, outputFile.length(), t);
    }

    private void giveTextReponse(HttpExchange t) {
        String filename = t.getRequestURI().toString();
        if (filename.equals("/")) {
            filename = "/index.html";
        }
        File outputFile = new File(Server.CONTENT_DIR, filename);

        if (!outputFile.exists())
            sendResponseAndClose(404, "File doesn't exist!", t);

        String response = null;
        try {
            response = FileUtils.readFileToString(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendResponseAndClose(200, response, t);
    }
}
