package fel.cvut.cz.handling;

import fel.cvut.cz.access.AccessHandler;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

import java.io.File;

/**
 * Created by marek on 21.5.16.
 */
public class PutHandler implements HttpSocketServerHandler {

    @Override
    public void handle(HttpSocketServerRequest request, HttpSocketServerResponse response) {
        HttpExchangeSerivce service = new HttpExchangeSerivce(request, response);
        if (!AccessHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
            service.sendTextResponseAndClose(403, "Access restricted!");
            return;
        }
        File fileToLoad = new File(service.getTargetFile());
        service.saveFileFromRequestHeader(fileToLoad);
        service.sendTextResponseAndClose(202, "Got the file you sent me, thank you!");
    }

}