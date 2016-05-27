package fel.cvut.cz.handling;

import fel.cvut.cz.FileCacheService;
import fel.cvut.cz.access.AccessHandler;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by marek on 21.5.16.
 */
public class GetHandler implements HttpSocketServerHandler {

    @Override
    public void handle(HttpSocketServerRequest request, HttpSocketServerResponse response) {
        HttpExchangeSerivce service = new HttpExchangeSerivce(request, response);

        giveResponse(service);
    }

    private void giveResponse(HttpExchangeSerivce service) {
        if (!AccessHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
            service.sendTextResponseAndClose(403, "Access restricted!");
            return;
        } else if (!FileCacheService.getInstance().exists(service.getTargetFile())) {
            service.sendTextResponseAndClose(404, "File doesn't exist!");
            return;
        }

        File outputFile = FileCacheService.getInstance().get(service.getTargetFile());
        if (!service.isTextNotFile()) {
            // add the required response header for binary files
            System.out.println("Sending binary!");
            service.addResponseHeader("Content-Type", "attachement; application/x-www-form-urlencoded");
        }
        // TODO: binary content looks like text on output
        byte[] bytearray = new byte[(int) outputFile.length()];
        try (FileInputStream fis = new FileInputStream(outputFile);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            bis.read(bytearray, 0, bytearray.length);
            service.sendResponseAndClose(200, bytearray, outputFile.length());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            service.sendTextResponseAndClose(500, "Serverside error, sorry!");
            return;
        }

    }

}
