import fel.cvut.cz.Server;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by marek on 22.5.16.
 */
public class DeleteHandlerTest extends AbstractTest {


    @Test
    public void testHandle() throws Exception {
        createTestingFile(Server.FILES_DIR + "/test_file_to_be_deleted.txt");

        String url = "http://localhost:8000/test_file_to_be_deleted.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Content-Type", "*/*");
        con.setRequestProperty("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'DELETE' request to URL : " + url);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();
        assertTrue(responseCode == 200);
        assertFalse(new File("files/test_file_to_be_deleted.txt").exists());
    }

    @Test
    public void testHandleNonExistingFile() throws IOException {
        String url = "http://localhost:8000/test_file_to_be_deleted.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Content-Type", "*/*");
        con.setRequestProperty("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'DELETE' request to URL : " + url);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getErrorStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();

        assertTrue(responseCode == 404);
    }
}