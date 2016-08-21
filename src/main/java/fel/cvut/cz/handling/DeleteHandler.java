package fel.cvut.cz.handling;

import fel.cvut.cz.FileCacheService;
import fel.cvut.cz.access.AccessHandler;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Deletes specified file on HTTP DELETE request. Checks permission for given location.
 * <p>
 * Created by marek on 21.5.16.
 */
public class DeleteHandler implements HttpSocketServerHandler {
    private static final Logger logger = Logger.getLogger(DeleteHandler.class.getName());


    @Override
    public void handle(HttpSocketServerRequest request, HttpSocketServerResponse response) {
        logger.fine("Handling DELETE request on " + request.getRequestURI());

        HttpExchangeSerivce service = new HttpExchangeSerivce(request, response);
        try {
            if (!AccessHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
                service.sendTextResponseAndClose(403, "Access restricted!");
                return;
            } else if (!FileCacheService.getInstance().exists(service.getTargetFile())) {
                service.sendTextResponseAndClose(404, "File doesn't exist!");
                return;
            }
            boolean deleted = Files.deleteIfExists(Paths.get(service.getTargetFile()));
            FileCacheService.getInstance().remove(service.getTargetFile());

            service.sendTextResponseAndClose(200, "Deleted!");
            logger.fine("DELETE on " + request.getRequestURI() + " discharged.");
            return;
        } catch (IOException x) {
            service.sendTextResponseAndClose(500, "Serverside error, sorry!");
            logger.fine(service.getTargetFile() + " couldn't be deleted!");
            return;
        }
    }

}
