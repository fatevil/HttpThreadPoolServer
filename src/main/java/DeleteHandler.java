import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * Created by marek on 21.5.16.
 */
public class DeleteHandler extends AbstractHttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        String filename = t.getRequestURI().toString();
        try {
            String fullFileName = String.format("%s%s", Server.FILES_DIR, filename);



            Files.delete(Paths.get(fullFileName));
            FileCacheService.getInstance().removeFile(fullFileName);
            System.out.printf("Succesfully deleted %s%n", fullFileName);
            sendResponseAndClose(200, "Deleted!", t);
        } catch (NoSuchFileException x) {
            System.err.format(String.format("%%s: no such file or directory%%n"), filename);
        } catch (IOException x) {
            System.err.println(x);
        }



        sendResponseAndClose(300, "Serverside error, sorry!", t);
    }

}
