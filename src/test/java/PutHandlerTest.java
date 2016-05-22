import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by marek on 22.5.16.
 */
public class PutHandlerTest {

    private static Server server;

    @BeforeClass
    public static void setUp() throws IOException {
        server = new Server();
        Thread t = new Thread(server);
        server.run();
    }

    @AfterClass
    public static void tearDown() {
        server.terminate();
        new File("forbidden_folder/tested_file.txt").delete();
        new File("tested_file.txt").delete();
    }

    private void createTestingFile(String name) {
        Path file = Paths.get(String.format("%s", name));
        if (Files.exists(file)) {
            return;
        }

        List<String> lines = Arrays.asList("Hi there! This is a test file and it should be retrieved by remote client!");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.printf("Created %s in %s%n", name, Server.FILES_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        byte[] encoded = Files.readAllBytes(Paths.get(String.format("tested_file.txt")));
        String r = new String(encoded);

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

        byte[] encoded = Files.readAllBytes(Paths.get(String.format("tested_file.txt")));
        String r = new String(encoded);

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