package fel.cvut.cz;

import com.sun.net.httpserver.HttpExchange;
import fel.cvut.cz.utils.RestrictedAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by marek on 21.5.16.
 */
public class DeleteHandler extends AbstractHttpHandler {
    @Override
    public void handle(HttpExchange t) {
        String fullFileName = null;
        String filename = t.getRequestURI().toString();
        try {
            checkPermission(t);

            fullFileName = String.format("%s%s", Server.FILES_DIR, filename);
            boolean deleted = Files.deleteIfExists(Paths.get(fullFileName));
            FileCacheService.getInstance().removeFile(fullFileName);

            if (deleted) {
                System.out.printf("Succesfully deleted %s%n", fullFileName);
                sendResponseAndClose(200, "Deleted!", t);
            } else {
                System.out.printf("File %s couldn't be deleted, because it doesn't exist!%n", fullFileName);
                sendResponseAndClose(404, "File not found!", t);
            }

            return;
        } catch (IOException x) {
            System.out.printf("File %s couldn't be deleted, because it doesn't exist!%n", fullFileName);
            System.out.println("out here");
            sendResponseAndClose(404, "Not found!", t);
        } catch (RestrictedAccessException e) {
            System.out.println(e.getMessage());
            sendResponseAndClose(403, "Access resricted!", t);
            return;
        }

        sendResponseAndClose(500, "Serverside error, sorry!", t);
    }

}
