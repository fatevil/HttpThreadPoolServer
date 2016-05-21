import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

/**
 * Created by marek on 21.5.16.
 */
public class PutHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {


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
            String filename = contentDisposition.substring(fileNamePosition + 9);

            File outputFile =
                    new File(Server.FILES_DIR, filename);

            if (!outputFile.isFile() && !outputFile.createNewFile()) {
                throw new IOException("Error creating new file: " + outputFile.getAbsolutePath());
            }

            FileWriter out =
                    new FileWriter(outputFile);

            while ((i = reader.read()) != -1) {
                out.write(i);
            }

            out.close();
            in.close();

            System.out.printf("We recieved file \"%s\"!%n", filename);

        } catch (IOException e) {
            e.printStackTrace();
        }


        String response = "Got the file you sent me, thank you!";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
