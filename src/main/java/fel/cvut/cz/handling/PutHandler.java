package fel.cvut.cz.handling;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fel.cvut.cz.access.AccesHandler;

import java.io.File;

/**
 * Created by marek on 21.5.16.
 */
public class PutHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) {
        HttpExchangeSerivce service = new HttpExchangeSerivce(t);

        if (!AccesHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
            service.sendTextResponseAndClose(403, "Access restricted!");
            return;
        }
        File fileToLoad = new File(service.getTargetFile());
        service.saveFileFromRequestHeader(fileToLoad);

        service.sendTextResponseAndClose(202, "Got the file you sent me, thank you!");
    }

}