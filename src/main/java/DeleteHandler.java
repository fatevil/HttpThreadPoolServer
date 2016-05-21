import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * Created by marek on 21.5.16.
 */
public class DeleteHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        String response;
        if (delete(t.getRequestURI().toString())) {
            response = "Deleted!";
            t.sendResponseHeaders(200, response.length());
        } else {
            response = "It didn't work out! I'm sorry :(.";
            t.sendResponseHeaders(300, response.length());
        }
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public boolean delete(String path) {
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
