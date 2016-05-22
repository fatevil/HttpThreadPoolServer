import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by marek on 22.5.16.
 */
public class GetHandlerTest {
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
    }

    @Test
    public void testHandle() throws Exception {
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

        byte[] encoded = Files.readAllBytes(Paths.get("web_content/index.html"));
        String r = new String(encoded);

        assertTrue(responseCode == 200);
        assertTrue(r.equals(response.toString()));
    }

}