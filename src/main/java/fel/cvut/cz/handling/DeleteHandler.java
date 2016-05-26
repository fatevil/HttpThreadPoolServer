package fel.cvut.cz.handling;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fel.cvut.cz.FileCacheService;
import fel.cvut.cz.access.AccesHandler;
import fel.cvut.cz.utils.HttpExchangeSerivce;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by marek on 21.5.16.
 */
public class DeleteHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) {
        HttpExchangeSerivce service = new HttpExchangeSerivce(t);

        try {
            if (!AccesHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
                service.sendTextResponseAndClose(403, "Access restricted!");
                return;
            } else if (!FileCacheService.getInstance().exists(service.getTargetFile())) {
                service.sendTextResponseAndClose(404, "File doesn't exist!");
                return;
            }
            boolean deleted = Files.deleteIfExists(Paths.get(service.getTargetFile()));
            FileCacheService.getInstance().remove(service.getTargetFile());

            System.out.printf("%s deleted!%n", service.getTargetFile());
            service.sendTextResponseAndClose(200, "Deleted!");
            return;
        } catch (IOException x) {
            service.sendTextResponseAndClose(500, "Serverside error, sorry!");
            return;
        }
    }

}
