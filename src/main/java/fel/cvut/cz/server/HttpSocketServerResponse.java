package fel.cvut.cz.server;

import com.sun.net.httpserver.Headers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by marek on 27.5.16.
 */
public class HttpSocketServerResponse {
    private static final Logger logger = Logger.getLogger(HttpSocketServerResponse.class.getName());
    private final Headers headers = new Headers();
    private final Socket connection;
    private DataOutputStream writer;
    private int code;
    private long lengthOfOutputFile;


    /**
     * Default constructor specifying socket to send the response to.
     *
     * @param connection socket containing OutputStream to use for sending response
     * @throws IOException is thrown if the socket is already closed
     */
    public HttpSocketServerResponse(Socket connection) throws IOException {
        this.connection = connection;
        if (connection.getOutputStream() != null) {
            writer = new DataOutputStream(connection.getOutputStream());
        }
    }

    /**
     * Gets OutputStream that should be the response sent to.
     *
     * @return put response to this
     * @throws IOException is thrown if the stream is already closed
     */
    public OutputStream getResponseBody() throws IOException {
        return connection.getOutputStream();
    }

    /**
     * Creates structured response header.
     *
     * @param code               http response code
     * @param lengthOfOutputFile length of following response body
     * @throws HttpSocketServerException is thrown if the output stream is unreachable
     * @throws IOException               is thrown when trying to write to unreachable stream
     */
    public void sendResponseHeaders(int code, long lengthOfOutputFile) throws HttpSocketServerException, IOException {
        this.code = code;
        this.lengthOfOutputFile = lengthOfOutputFile;

        logger.info("Creating response header with code " + code + " and " + lengthOfOutputFile + " bytes long body.");

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

        logger.info("Response header created.");
    }

    /**
     * Write bytes to response OutputStream.
     *
     * @param line string to be written
     * @throws IOException is thrown when trying to write to unreachable stream
     */
    private void writeLine(String line) throws IOException {
        writer.writeBytes(line + "\n");
        logger.finest("Response OutputStream writing: '" + line + "'");
    }

    /**
     * Gets object containing response headers.
     */
    public Headers getResponseHeaders() {
        return headers;
    }
}
