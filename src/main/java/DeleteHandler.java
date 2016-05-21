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
            sendResponseAndClose(300, "It didn't work out! I'm sorry :(.", t);
        }
    }

    private boolean delete(String path) {
        try {
            Files.delete(Paths.get(String.format("%s%s", Server.FILES_DIR, path)));
            System.out.printf("Succesfully deleted %s%s%n", Server.FILES_DIR, path);
            return true;
        } catch (NoSuchFileException x) {
            System.err.format(String.format("%%s: no such file or directory%%n"), path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
        return false;
    }
}
