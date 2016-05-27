package fel.cvut.cz.server;

import com.sun.net.httpserver.Headers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by marek on 27.5.16.
 */
public class HttpSocketServerResponse {
    private final Headers headers = new Headers();
    private final Socket connection;
    private DataOutputStream writer;
    private int code;
    private long lengthOfOutputFile;


    public HttpSocketServerResponse(Socket connection) throws IOException {
        this.connection = connection;
        if (connection.getOutputStream() != null) {
            writer = new DataOutputStream(connection.getOutputStream());
        }
    }

    public OutputStream getResponseBody() throws IOException {
        return connection.getOutputStream();
    }

    public void sendResponseHeaders(int code, long lengthOfOutputFile) throws HttpSocketServerException, IOException {
        this.code = code;
        this.lengthOfOutputFile = lengthOfOutputFile;

        if (connection == null) {
            throw new HttpSocketServerException("Connection not found!");
        } else if (connection.isClosed()) {
            throw new HttpSocketServerException("Connection closed!");
        } else if (writer == null) {
            throw new HttpSocketServerException("OutputStream not specified!");
        }

        writeLine("HTTP/1.1 " + code);
        writeLine("Connection: close");

        if (lengthOfOutputFile != 0) {
            writeLine("Content-Length: " + lengthOfOutputFile);
        }

        // Send headers
        if (!this.headers.isEmpty()) {

            StringBuilder b = new StringBuilder();
            headers.forEach((s, list) -> {
                while (!list.isEmpty()) {
                    b.append(s);
                    b.append(": ");
                    b.append(list.remove(0));
                    b.append("\n");
                }
            });

            writeLine(b.toString());
        }

        // according to protocol, here should be a blank line
        writeLine("");

    }

    private void writeLine(String line) throws IOException {
        writer.writeBytes(line + "\n");
    }

    public Headers getResponseHeaders() {
        return headers;
    }
}
