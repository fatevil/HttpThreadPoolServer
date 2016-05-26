package fel.cvut.cz;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.FileUtils;
import fel.cvut.cz.utils.RestrictedAccessException;

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
        try {
            System.out.println("Gonna give file!");
            checkPermission(t);

            String filename = t.getRequestURI().toString();
            String fullFileName = String.format("%s%s", Server.FILES_DIR, filename);

            if (!FileCacheService.getInstance().fileExists(fullFileName)) {
                sendResponseAndClose(404, "File doesn't exist!", t);
                return;
            }

            File outputFile = FileCacheService.getInstance().getFile(fullFileName);

            // add the required response header for a PDF file
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "application/x-www-form-urlencoded");

            byte[] bytearray = new byte[(int) outputFile.length()];


            try (FileInputStream fis = new FileInputStream(outputFile);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                bis.read(bytearray, 0, bytearray.length);
            }


            sendDataAndClose(200, bytearray, outputFile.length(), t);
        } catch (RestrictedAccessException e) {
            System.out.println(e.getMessage());
            sendResponseAndClose(403, "Access resricted!", t);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponseAndClose(500, "Serverside error, sorry!", t);
        }
    }

    private void giveTextReponse(HttpExchange t) {
        System.out.println("Gonna give static content!");
        String filename = t.getRequestURI().toString();
        if (filename.equals("/")) {
            filename = "/index.html";
        }
        String fullFileName = String.format("%s%s", Server.CONTENT_DIR, filename);

        if (!FileCacheService.getInstance().fileExists(fullFileName)) {
            sendResponseAndClose(404, "File doesn't exist!", t);
            return;
        }

        File outputFile = FileCacheService.getInstance().getFile(fullFileName);
        String response = null;
        try {
            response = FileUtils.readFileToString(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            sendResponseAndClose(500, "Serverside error, sorry!", t);
            return;
        }
        sendResponseAndClose(200, response, t);
    }
}
