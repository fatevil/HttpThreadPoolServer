import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

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


        URL url = new URL("http://localhost:8000/sendTestFile.txt");
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestProperty(
                "Content-Type", "application/x-www-form-urlencoded");
        httpCon.setRequestMethod("DELETE");
        httpCon.connect();
        System.out.println("connected");
    }

    @AfterClass
    public static void tearDown() {
        server.terminate();
    }

    @Test
    public void testHandle() {
        URL url = null;
        try {
            url = new URL("http://localhost:8000/sendTestFile.txt");
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        DataOutputStream dataOutputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setRequestProperty("Authorization", "dXNlcjpwYXNzd29yZA==");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(String.valueOf(Files.readAllBytes(Paths.get("test_files/sendTestFile.txt"))));
            httpURLConnection.connect();
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.flush();
                    dataOutputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

}