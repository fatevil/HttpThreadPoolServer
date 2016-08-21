package fel.cvut.cz;

import fel.cvut.cz.handling.*;
import fel.cvut.cz.server.HttpSocketServer;
import fel.cvut.cz.server.HttpSocketServerException;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;
import fel.cvut.cz.utils.CustomFileUtils;

import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * fel.cvut.cz.Server listening on localhost. Responses to GET, PUT, DELETE methods. Uses basic authentication and ThreadPool executor.
 * <p>
 * Example usage:
 * <p>
 * <b>To REMOVE file</b><p>
 * Carry out <b>DELETE</b> request:<p>
 * http://localhost:8000/bobek.c<p>
 * <p>
 * <b>To ADD file</b><p>
 * Carry out <b>PUT</b> request with attachement "bobek.c":<p>
 * http://localhost:8000/bobek.c<p>
 * <p>
 * <b>To GET static content</b><p>
 * Carry out <b>GET</b> request:<p>
 * http://localhost:8000/index.html<p>
 * <p>
 * <b>To GET file</b><p>
 * Carry out <b>GET</b> following request with header Accept : application/x-www-form-urlencoded<p>
 * http://localhost:8000/bobek.c<p>
 * <p>
 * <p>
 * Created by marek on 21.5.16.
 */
public class Server implements Runnable {
    public static final int SERVER_PORT = 8000;
    public static final String FILES_DIR = "files";
    public static final String CONTENT_DIR = "web_content";
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private HttpSocketServer httpServer;

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.run();
    }

    /**
     * Creates folder with restricted access.
     */
    public static void setupFolders() {
        CustomFileUtils.createDirIfNotExists(FILES_DIR);
        CustomFileUtils.createDirIfNotExists(String.format("%s/forbidden_folder", FILES_DIR));
        CustomFileUtils.createDirIfNotExists(CONTENT_DIR);
        CustomFileUtils.putHtaccessToDir(String.format("%s/forbidden_folder", FILES_DIR));
    }

    /**
     * Instantiates HttpSocketServer, creates content folders and sets fixed-size thread pool executor for incoming connections.
     */
    @Override
    public void run() {
        httpServer = new HttpSocketServer(SERVER_PORT);
        httpServer.setHandler(new RequestHandler());

        setupFolders();
        try {
            httpServer.setExecutor(Executors.newFixedThreadPool(50)); // creates a default executor
        } catch (HttpSocketServerException e) {
            logger.log(Level.SEVERE, "Could not start server!", e);

        }
        httpServer.start();
        logger.info("Server started on port " + SERVER_PORT);
    }

    /**
     * Immediately stop server and executing tasks.
     */
    public void terminate() {
        httpServer.stop();
    }

    static class RequestHandler implements HttpSocketServerHandler {
        @Override
        public void handle(HttpSocketServerRequest httpSocketServerRequest, HttpSocketServerResponse httpSocketServerResponse) {
            switch (httpSocketServerRequest.getRequestMethod()) {
                case "GET":
                    new GetHandler().handle(httpSocketServerRequest, httpSocketServerResponse);
                    break;
                case "DELETE":
                    new DeleteHandler().handle(httpSocketServerRequest, httpSocketServerResponse);
                    break;
                case "PUT":
                    new PutHandler().handle(httpSocketServerRequest, httpSocketServerResponse);
                    break;
                default:
                    new HttpExchangeSerivce(httpSocketServerRequest, httpSocketServerResponse).sendTextResponseAndClose(405, String.format("Request method %s is not allowed!", httpSocketServerRequest.getRequestMethod()));
                    break;
            }
        }
    }
}
