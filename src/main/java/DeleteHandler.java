import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * Created by marek on 21.5.16.
 */
public class DeleteHandler extends AbstractHttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        String response;
        if (delete(t.getRequestURI().toString())) {
            sendResponseAndClose(200, "Deleted!", t);
        } else {
            sendResponseAndClose(300, "Serverside error, sorry!", t);
        }
    }

    private boolean delete(String filename) {
        try {
            String fullFileName = String.format("%s%s", Server.FILES_DIR, filename);
            Files.delete(Paths.get(fullFileName));
            FileCacheService.getInstance().removeFile(fullFileName);
            System.out.printf("Succesfully deleted %s%n", fullFileName);
            return true;
        } catch (NoSuchFileException x) {
            System.err.format(String.format("%%s: no such file or directory%%n"), filename);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", filename);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
        return false;
    }
}
