package fel.cvut.cz;

import fel.cvut.cz.handling.*;
import fel.cvut.cz.server.HttpSocketServer;
import fel.cvut.cz.server.HttpSocketServerException;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;
import fel.cvut.cz.utils.CustomFileUtils;

import java.util.concurrent.Executors;


/**
 * fel.cvut.cz.Server listening on localhost. Responses to GET, PUT, DELETE methods. Uses basic authentification and ThreadPool executor.
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

    private HttpSocketServer httpServer;

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.run();
    }

    public static void setupFolders() {
        CustomFileUtils.createDirIfNotExists(FILES_DIR);
        CustomFileUtils.createDirIfNotExists(String.format("%s/forbidden_folder", FILES_DIR));
        CustomFileUtils.createDirIfNotExists(CONTENT_DIR);
        CustomFileUtils.putHtaccessToDir(String.format("%s/forbidden_folder", FILES_DIR));
    }

    @Override
    public void run() {
        httpServer = new HttpSocketServer(SERVER_PORT);
        httpServer.setHandler(new RequestHandler());

        setupFolders();
        try {
            httpServer.setExecutor(Executors.newFixedThreadPool(100)); // creates a default executor
        } catch (HttpSocketServerException e) {
            e.printStackTrace();
        }
        httpServer.start();

    }

    public void terminate() {
        httpServer.stop();
    }

    static class RequestHandler implements HttpSocketServerHandler {
        @Override
        public void handle(HttpSocketServerRequest httpSocketServerRequest, HttpSocketServerResponse httpSocketServerResponse) {
            System.out.println("Made connection!");

            switch (httpSocketServerRequest.getRequestMethod()) {
                case "GET":
                    new GetHandler().handle(httpSocketServerRequest, httpSocketServerResponse);
                    return;
                case "DELETE":
                    new DeleteHandler().handle(httpSocketServerRequest, httpSocketServerResponse);
                    return;
                case "PUT":
                    System.out.println("at least im gonna try");
                    new PutHandler().handle(httpSocketServerRequest, httpSocketServerResponse);
                    return;
                default:
                    new HttpExchangeSerivce(httpSocketServerRequest, httpSocketServerResponse).sendTextResponseAndClose(405, String.format("Request method %s is not allowed!", httpSocketServerRequest.getRequestMethod()));
                    return;
            }
        }
    }
}
