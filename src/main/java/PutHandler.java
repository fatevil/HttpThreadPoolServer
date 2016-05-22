import com.sun.net.httpserver.HttpExchange;

import java.io.*;

/**
 * Created by marek on 21.5.16.
 */
public class PutHandler extends AbstractHttpHandler {

    @Override
    public void handle(HttpExchange t) {


        try {
            int i;
            InputStream input;
            input = t.getRequestBody();
            BufferedInputStream in =
                    new BufferedInputStream(input);
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(in));

            String contentDisposition = t.getRequestHeaders().getFirst("Content-Disposition");
            int fileNamePosition = contentDisposition.lastIndexOf("filename");

            String fullFileName;
            if (fileNamePosition == 0) {
                String filename = contentDisposition.substring(fileNamePosition + 9);
                fullFileName = String.format("%s/%s", Server.FILES_DIR, filename);
            } else {
                fullFileName = String.format("%s/%s", Server.FILES_DIR, t.getRequestURI().toString());
            }

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
        }

        sendResponseAndClose(200, "Got the file you sent me, thank you!", t);
    }

}
