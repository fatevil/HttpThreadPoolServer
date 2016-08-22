package fel.cvut.cz.handling;

import fel.cvut.cz.access.AccessHandler;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

import java.io.File;
import java.util.logging.Logger;

/**
 * Retrieves file from client and gives response on HTTP PUT request. Checks permission on specified folder.
 * <p>
 * Created by marek on 21.5.16.
 */
public class PutHandler implements HttpSocketServerHandler {
    private static final Logger logger = Logger.getLogger(PutHandler.class.getName());

    /**
     * Creates file from input stream using HttpExchangeService, gives response.
     *
     * @param request  contains task specifics
     * @param response performs response action
     */
    @Override
    public void handle(HttpSocketServerRequest request, HttpSocketServerResponse response) {
        logger.fine("Handling PUT request on " + request.getRequestURI());

        HttpExchangeSerivce service = new HttpExchangeSerivce(request, response);
        if (!AccessHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
            service.sendTextResponseAndClose(403, "Access restricted!");
            logger.fine("Access to requested location is restricted, no file accepted!");
            return;
        }
        File fileToLoad = new File(service.getTargetFile());
        service.saveFileFromRequestHeader(fileToLoad);
        service.sendTextResponseAndClose(202, "Got the file you sent me, thank you!");
        logger.fine("Response to PUT request sent.");
    }

}