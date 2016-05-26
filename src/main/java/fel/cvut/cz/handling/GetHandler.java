package fel.cvut.cz.handling;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fel.cvut.cz.FileCacheService;
import fel.cvut.cz.access.AccesHandler;
import fel.cvut.cz.utils.HttpExchangeSerivce;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by marek on 21.5.16.
 */
public class GetHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) {
        HttpExchangeSerivce service = new HttpExchangeSerivce(t);

        giveResponse(service);
    }

    private void giveResponse(HttpExchangeSerivce service) {
        if (!AccesHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
            service.sendTextResponseAndClose(403, "Access restricted!");
            return;
        } else if (!FileCacheService.getInstance().exists(service.getTargetFile())) {
            service.sendTextResponseAndClose(404, "File doesn't exist!");
            return;
        }

        File outputFile = FileCacheService.getInstance().get(service.getTargetFile());
        if (!service.textNotFile()) {
            // add the required response header for a PDF file
            service.addResponseHeader("Content-Type", "application/x-www-form-urlencoded");
            return;
        }

        byte[] bytearray = new byte[(int) outputFile.length()];
        try (FileInputStream fis = new FileInputStream(outputFile);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            bis.read(bytearray, 0, bytearray.length);
            System.out.println("File saved!");
        } catch (IOException e) {
            e.printStackTrace();
            service.sendTextResponseAndClose(500, "Serverside error, sorry!");
            return;
        }
        service.sendResponseAndClose(200, bytearray, outputFile.length());
    }

}
