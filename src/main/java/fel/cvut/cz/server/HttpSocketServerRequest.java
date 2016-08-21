package fel.cvut.cz.server;

import com.sun.net.httpserver.Headers;
import fel.cvut.cz.handling.HttpSocketServerHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Run this class to parse HttpRequest from socket InputStream, put it into nice a form and make response with given handler.
 * <p>
 * Created by marek on 27.5.16.
 */
public class HttpSocketServerRequest implements Runnable {

    private static final Logger logger = Logger.getLogger(HttpSocketServerRequest.class.getName());
    private final HttpSocketServerHandler handler;
    private final Socket connection;
    private final Headers headers = new Headers();
    private String requestMethod;
    private String requestURI;
    private String requestProtocol;
    private InputStream requestBody;

    /**
     * Returns nicely represented http request.
     *
     * @param handler    object solving requested tasks
     * @param connection socket to read input from
     */
    public HttpSocketServerRequest(HttpSocketServerHandler handler, Socket connection) {
        this.handler = handler;
        this.connection = connection;
    }

    /**
     * Gets string specifying request method like GET, PUT etc.
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    public Headers getRequestHeaders() {
        return headers;
    }

    /**
     * Gets exact requested address.
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Returns part of the request behind headers - mostly files.
     */
    public InputStream getRequestBody() {
        return requestBody;
    }

    /**
     * Creates object for making response, parses request and invokes handler.
     */
    @Override
    public void run() {
        try {
            HttpSocketServerResponse response = new HttpSocketServerResponse(connection);
            parseRequest();
            handler.handle(this, response);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpSocketServerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Goes through InputStream and makes object representation of it.
     *
     * @throws IOException               is thrown if the input stream is empty or already closed
     * @throws HttpSocketServerException is thrown when http protocol is violated
     */
    public void parseRequest() throws IOException, HttpSocketServerException {
        // Used to read in from the socket
        BufferedReader input = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        // check if stream returns anything but null
        String line = input.readLine();
        if (line == null) {
            throw new HttpSocketServerException("Input is empty!");
        }

        // skip empty lines
        while (line != null && line.isEmpty()) {
            line = input.readLine();
        }

        // first line contains METHOD URI PROTOCOL
        setFirstLine(line);

        // get headers
        for (String l = input.readLine(); l != null && !l.isEmpty(); l = input.readLine()) {
            String[] items = l.split(": ");

            if (items.length == 1) {
                throw new HttpSocketServerException(String.format("Protocol violated at\t %s!\n", l));
            }

            String value = items[1];
            for (int i = 2; i < items.length; i++) {
                value += ": " + items[i];
            }
            if (!headers.containsKey(items[0])) {
                headers.put(items[0], new ArrayList<String>());
            }
            headers.get(items[0]).add(items[1]);
        }

        // read data if content-length is specified
        if (isTypeContainingData() && headers.containsKey("Content-Length")) {
            int length = Integer.parseInt(headers.getFirst("Content-Length"));
            int index = 0;
            int i;
            // TODO: is this a correct solution?
            byte[] bytes = new byte[length];
            while ((input.ready())) {
                i = input.read();
                bytes[index] = (byte) i;
                index++;
            }

            requestBody = new ByteArrayInputStream(bytes);
        }
    }

    private void setFirstLine(String line) throws HttpSocketServerException {
        String[] s = line.split(" ");
        if (s.length != 3) {
            throw new HttpSocketServerException("First line doesnt contain 3 strings.");
        }

        requestMethod = s[0];
        requestURI = s[1];
        requestProtocol = s[2];
    }

    private boolean isTypeContainingData() throws HttpSocketServerException {
        if (requestMethod == null) {
            throw new HttpSocketServerException("Request method is not specified!");
        }
        switch (requestMethod) {
            case "PUT":
                return true;
            default:
                return false;
        }

    }
}
