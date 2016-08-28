import fel.cvut.cz.Server;
import fel.cvut.cz.utils.CustomFileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
 * // TODO: Put individual functions to separate test classes
 * <p>
 * Created by marek on 22.5.16.
 */
public class GetHandlerTest {
    private static final Logger logger = Logger.getLogger(GetHandlerTest.class.getName());

    private static Server server;

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {
        /* Start server, create necessary folders and wait for connection. */
        logger.info("Setting up abstract test class!");
        server = new Server(8001);
        Thread t = new Thread(server);
        t.start();

        CustomFileUtils.createDirIfNotExists(String.format("%s/forbidden_folder_test", Server.FILES_DIR));
        CustomFileUtils.putHtaccessToDir(String.format("%s/forbidden_folder_test", Server.FILES_DIR));
        logger.info("Abstract test class set up.");
        Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        /* Shutdown server and clean up.*/
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
        /* Send GET request and expect response code 200. Accepted file should be the same bytes as file located on disc.*/
        String url = "http://localhost:8001/index.html";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        logger.info("\nSending 'GET' request to URL : " + url);

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
        /* Send GET request on file with restricted access and expect response code 200. Accepted file should be the same bytes as file located on disc.*/
        createTestingFile(Server.FILES_DIR + "/forbidden_folder/tested_file.txt");

        String url = "http://localhost:8001/forbidden_folder/tested_file.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic dXNlcjpwYXNzd29yZA==");
        con.setRequestProperty("Accept", "application/x-www-form-urlencoded");

        int responseCode = con.getResponseCode();
        logger.info("\nSending 'GET' request to URL : " + url);

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
        /* Send GET request on file with restricted access and expect response code 403. File should not be retrieved because of unauthorized access.*/
        CustomFileUtils.createTestingFile(Server.FILES_DIR + "/forbidden_folder/tested_file.txt");

        String url = "http://localhost:8001/forbidden_folder/tested_file.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/x-www-form-urlencoded");

        int responseCode = con.getResponseCode();
        logger.info("\nSending 'GET' request to URL : " + url);

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