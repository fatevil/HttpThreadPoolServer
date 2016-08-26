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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by marek on 22.5.16.
 */
public class PutHandlerTest {
    private static final Logger logger = Logger.getLogger(PutHandlerTest.class.getName());

    private static Server server;

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {
        //TODO:tests wont work when they're all instantiated at once, it must be done one by one (classes)

        logger.info("Setting up abstract test class!");
        server = new Server(8002);
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
            Files.deleteIfExists(Paths.get("tested_file.txt"));
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test/.htaccess", Server.FILES_DIR)));
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test/tested_file.txt", Server.FILES_DIR)));
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test", Server.FILES_DIR)));
            logger.info("Test files deleted!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Test class couldn't delete test files!", e);
        }
    }

    @Test
    public void testHandlePut() throws Exception {
        CustomFileUtils.createTestingFile("tested_file.txt");
        String url = "http://localhost:8002/forbidden_folder_test/tested_file.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "*/*");
        con.setRequestProperty("Authorization", "Basic dXNlcjpwYXNzd29yZA==");
        con.setDoOutput(true);

        byte[] encoded = Files.readAllBytes(Paths.get(String.format("tested_file.txt")));
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
        assertArrayEquals(encoded, Files.readAllBytes(Paths.get(String.format("tested_file.txt"))));
    }

    @Test
    public void testHandlePutRestricted() throws Exception {
        CustomFileUtils.createTestingFile("tested_file.txt");
        String url = "http://localhost:8002/forbidden_folder_test/tested_file.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "*/*");
        con.setDoOutput(true);

        byte[] encoded = Files.readAllBytes(Paths.get("tested_file.txt"));
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
        File f = new File(String.format("%sforbidden_folder_test/tested_file.txt", Server.FILES_DIR));
        assertTrue(responseCode == 403);
        assertTrue(!f.exists());
    }
}