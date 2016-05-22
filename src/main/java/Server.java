import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import utils.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Server listening on localhost. Responses to GET, PUT, DELETE methods. Uses basic authentification and ThreadPool executor.
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
 * You can also use following if you add header "Content-Type : attachement; filename=bobek.c" to you request:<p>
 * http://localhost:8000/<p>
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

    static final int SERVER_PORT = 8000;

    static final String FILES_DIR = "files";
    static final String CONTENT_DIR = "web_content";

    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;

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
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

            HttpContext cc = server.createContext("/", new RequestHandler());
            cc.setAuthenticator(null);

            ExecutorService threadPoolExecutor =
                    new ThreadPoolExecutor(
                            corePoolSize,
                            maxPoolSize,
                            keepAliveTime,
                            TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>()
                    );


            Util.createDirIfNotExists(FILES_DIR);
            Util.createDirIfNotExists(String.format("%s/forbidden_folder", FILES_DIR));
            Util.createDirIfNotExists(CONTENT_DIR);
            Util.putHtaccessToDir(String.format("%s/forbidden_folder", FILES_DIR));

            server.setExecutor(threadPoolExecutor); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    return;
            }
        }
    }
}
