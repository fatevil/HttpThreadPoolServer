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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by marek on 22.5.16.
 */
public class DeleteHandlerTest {
    private static final Logger logger = Logger.getLogger(DeleteHandlerTest.class.getName());

    private static Server server;

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {
        logger.info("Setting up abstract test class!");
        server = new Server(8000);
        Thread t = new Thread(server);
        t.start();

        CustomFileUtils.createDirIfNotExists(String.format("%s/forbidden_folder_test", Server.FILES_DIR));
        CustomFileUtils.putHtaccessToDir(String.format("%s/forbidden_folder_test", Server.FILES_DIR));
        logger.info("Abstract test class set up.");
        Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        server.terminate();
        try {
            Files.deleteIfExists(Paths.get("test_file_to_be_deleted.txt"));
            logger.info("Test file deleted!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't delete test files!", e);
        }
    }

    @Test
    public void testHandle() throws Exception {
        CustomFileUtils.createTestingFile(Server.FILES_DIR + "/test_file_to_be_deleted.txt");

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
    public void testHandleNonExistingFile() throws IOException, InterruptedException {
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