import fel.cvut.cz.Server;
import fel.cvut.cz.utils.CustomFileUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fel.cvut.cz.utils.CustomFileUtils.createTestingFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by marek on 22.5.16.
 */
public class GetHandlerTest extends AbstractTest {
    private static final Logger logger = Logger.getLogger(GetHandlerTest.class.getName());

    @AfterClass
    public static void tearDown() {
        server.terminate();
        try {
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test/.htaccess", Server.FILES_DIR)));
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test/tested_file.txt", Server.FILES_DIR)));
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test", Server.FILES_DIR)));
            logger.info("Test files deleted!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Test class couldn't delete test files!", e);
        }
    }

    @Test
    public void testHandleStaticContent() throws Exception {
        String url = "http://localhost:8000/index.html";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();

        byte[] encoded = Files.readAllBytes(Paths.get(String.format("%s/index.html", Server.CONTENT_DIR)));
        String r = new String(encoded);

        assertTrue(responseCode == 200);
        assertTrue(r.equals(response.toString()));
    }

    @Test
    public void testHandleRestrictedFileSuccess() throws Exception {
        createTestingFile(Server.FILES_DIR + "/forbidden_folder/tested_file.txt");

        String url = "http://localhost:8000/forbidden_folder/tested_file.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic dXNlcjpwYXNzd29yZA==");
        con.setRequestProperty("Accept", "application/x-www-form-urlencoded");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        in.readLine();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();

        byte[] encoded = Files.readAllBytes(Paths.get(String.format("%s//forbidden_folder/tested_file.txt", Server.FILES_DIR)));
        String r = new String(encoded);

        assertTrue(responseCode == 200);
        assertTrue(r.equals(response.toString()));

    }

    @Test
    public void testHandleRestrictedFileFail() throws Exception {
        CustomFileUtils.createTestingFile(Server.FILES_DIR + "/forbidden_folder/tested_file.txt");

        String url = "http://localhost:8000/forbidden_folder/tested_file.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/x-www-form-urlencoded");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getErrorStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();

        assertTrue(responseCode == 403);
        assertFalse(new File("forbidden_folder/tested_file.txt").exists());
    }
}