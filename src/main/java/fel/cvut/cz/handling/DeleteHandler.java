package fel.cvut.cz.handling;

import fel.cvut.cz.FileCacheService;
import fel.cvut.cz.access.AccessHandler;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by marek on 21.5.16.
 */
public class DeleteHandler implements HttpSocketServerHandler {

    @Override
    public void handle(HttpSocketServerRequest request, HttpSocketServerResponse response) {
        HttpExchangeSerivce service = new HttpExchangeSerivce(request, response);
        try {
            System.out.println(request.getRequestURI());
            System.out.println(service.getTargetFile());
            if (!AccessHandler.check(service.getTargetDirectory(), service.getAuthorization())) {
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
