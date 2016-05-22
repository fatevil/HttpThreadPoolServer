import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by marek on 22.5.16.
 */
public class PutHandlerTest extends AbstractTest {


    @Test
    public void testHandlePut() throws Exception {
        createTestingFile("tested_file.txt");
        String url = "http://localhost:8000/forbidden_folder/tested_file.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("PUT");
        con.setRequestProperty("Authorization", "Basic dXNlcjpwYXNzd29yZA==");
        con.setDoOutput(true);

        byte[] encoded = Files.readAllBytes(Paths.get(String.format("%s/tested_file.txt", Server.FILES_DIR)));
        con.getOutputStream().write(encoded);


        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'PUT' request to URL : " + url);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();


        assertTrue(responseCode == 202);
        assertArrayEquals(encoded, Files.readAllBytes(Paths.get(String.format("%s/tested_file.txt", Server.FILES_DIR))));
    }

    @Test
    public void testHandlePutRestricted() throws Exception {
        createTestingFile("tested_file.txt");
        String url = "http://localhost:8000/forbidden_folder/tested_file.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("PUT");
        con.setDoOutput(true);

        byte[] encoded = Files.readAllBytes(Paths.get(String.format("%s/tested_file.txt", Server.FILES_DIR)));
        con.getOutputStream().write(encoded);


        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'PUT' request to URL : " + url);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getErrorStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();
        File f = new File("forbidden_folder/tested_file.txt");
        assertTrue(responseCode == 403);
        assertTrue(!f.exists());
    }
}