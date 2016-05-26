package fel.cvut.cz;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fel.cvut.cz.utils.CustomFileUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


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
    //    private static final Logger logger = Logger.getLogger(fel.cvut.cz.Server.class);
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    
    private HttpServer httpServer;

    public Server(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
    }

    public Server() {
        this.corePoolSize = 5;
        this.maxPoolSize = 100;
        this.keepAliveTime = 5000;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.run();
    }

    @Override
    public void run() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

            HttpContext cc = httpServer.createContext("/", new RequestHandler());
            cc.setAuthenticator(null);

            ExecutorService threadPoolExecutor =
                    new ThreadPoolExecutor(
                            corePoolSize,
                            maxPoolSize,
                            keepAliveTime,
                            TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>()
                    );


            CustomFileUtils.createDirIfNotExists(FILES_DIR);
            CustomFileUtils.createDirIfNotExists(String.format("%s/forbidden_folder", FILES_DIR));
            CustomFileUtils.createDirIfNotExists(CONTENT_DIR);
            CustomFileUtils.putHtaccessToDir(String.format("%s/forbidden_folder", FILES_DIR));

            httpServer.setExecutor(threadPoolExecutor); // creates a default executor
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void terminate() {
        httpServer.stop(1);
    }


    static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("Made connection!");

            switch (t.getRequestMethod()) {
                case "GET":
                    new GetHandler().handle(t);
                    return;
                case "DELETE":
                    new DeleteHandler().handle(t);
                    return;
                case "PUT":
                    new PutHandler().handle(t);
            }
        }
    }
}
