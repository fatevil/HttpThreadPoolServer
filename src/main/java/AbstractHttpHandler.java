import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by marek on 21.5.16.
 */
public abstract class AbstractHttpHandler implements HttpHandler {

    public void sendResponseAndClose(int code, String message, HttpExchange t) {
        try (OutputStream os = t.getResponseBody()) {
            t.sendResponseHeaders(code, message.length());
            os.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataAndClose(int code, byte[] bytearray, long lengthOfOutputFile, HttpExchange t) {
        try (OutputStream os = t.getResponseBody()) {
            t.sendResponseHeaders(200, lengthOfOutputFile);
            os.write(bytearray, 0, bytearray.length);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
