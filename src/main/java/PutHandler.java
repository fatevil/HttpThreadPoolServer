import com.sun.net.httpserver.HttpExchange;
import utils.RestrictedAccessException;

import java.io.*;

/**
 * Created by marek on 21.5.16.
 */
public class PutHandler extends AbstractHttpHandler {

    @Override
    public void handle(HttpExchange t) {
        try {
            checkPermission(t);

            int i;
            InputStream input;
            input = t.getRequestBody();
            BufferedInputStream in =
                    new BufferedInputStream(input);
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(in));

            String fullFileName = String.format("%s%s", Server.FILES_DIR, t.getRequestURI().toString());

            System.out.println(fullFileName);
            File outputFile =
                    FileCacheService.getInstance().createFile(fullFileName);

            FileWriter out =
                    new FileWriter(outputFile);

            while ((i = reader.read()) != -1) {
                out.write(i);
            }

            out.close();
            in.close();

            System.out.printf("We recieved file \"%s\"!%n", fullFileName);

        } catch (IOException e) {
            e.printStackTrace();
            sendResponseAndClose(300, "Serverside error, sorry!", t);
        } catch (RestrictedAccessException e) {
            System.out.println("Access restricted!");
            sendResponseAndClose(403, "Access resricted!", t);
            return;
        }

        sendResponseAndClose(200, "Got the file you sent me, thank you!", t);
    }

}
