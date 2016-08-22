package fel.cvut.cz.handling;

import fel.cvut.cz.FileCacheService;
import fel.cvut.cz.access.AccessHandler;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responses to HTTP GET request with static content or binary file. Checks permission on specified folder.
 * <p>
 * Created by marek on 21.5.16.
 */
public class GetHandler implements HttpSocketServerHandler {
    private static final Logger logger = Logger.getLogger(GetHandler.class.getName());

    /**
     * Sends file and feedback to client.
     *
     * @param request  contains task specifics
     * @param response performs response action
     */
    @Override
    public void handle(HttpSocketServerRequest request, HttpSocketServerResponse response) {
        logger.fine("Handling GET request on " + request.getRequestURI());

        HttpExchangeSerivce service = new HttpExchangeSerivce(request, response);
        if (!AccessHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
            service.sendTextResponseAndClose(403, "Access restricted!");
            logger.fine("Access restricted!");
            return;
        } else if (!FileCacheService.getInstance().exists(service.getTargetFile())) {
            service.sendTextResponseAndClose(404, "File doesn't exist!");
            logger.fine("File doesn't exist!");
            return;
        }

        File outputFile = FileCacheService.getInstance().get(service.getTargetFile());
        if (!service.isTextNotFile()) {
            // add required response header for binary files
            logger.finer("Sending binary file.");
            service.addResponseHeader("Content-Type", "attachement; application/x-www-form-urlencoded");
        }
        // TODO: binary content looks like text on output
        byte[] bytearray = new byte[(int) outputFile.length()];
        try (FileInputStream fis = new FileInputStream(outputFile);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            bis.read(bytearray, 0, bytearray.length);
            service.sendResponseAndClose(200, bytearray, outputFile.length());
            logger.fine("GET on " + request.getRequestURI() + " discharged.");
            return;
        } catch (IOException e) {
            service.sendTextResponseAndClose(500, "Serverside error, sorry!");
            logger.log(Level.INFO, "GET on " + request.getRequestURI() + " request couldn't be processed.", e);
            return;
        }
    }

}
