import com.sun.net.httpserver.*;

import java.io.File;
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
 * Carry out <b>GET</b> request:<p>
 * http://localhost:8000/bobek.c<p>
 * <p>
 * Created by marek on 21.5.16.
 */
public class Server implements Runnable {

    static final String FILES_DIR = "files";
    static final String CONTENT_DIR = "web_content";

    private final String USER_NAME = "user";
    private final String USER_PASSWORD = "password";

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
            server = HttpServer.create(new InetSocketAddress(8000), 0);

            HttpContext cc = server.createContext("/", new RequestHandler());
            cc.setAuthenticator(new BasicAuthenticator("test") {
                @Override
                public boolean checkCredentials(String user, String pwd) {
                    return user.equals(USER_NAME) && pwd.equals(USER_PASSWORD);
                }
            });


            ExecutorService threadPoolExecutor =
                    new ThreadPoolExecutor(
                            corePoolSize,
                            maxPoolSize,
                            keepAliveTime,
                            TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>()
                    );


            createDirIfNotExists(FILES_DIR);
            createDirIfNotExists(CONTENT_DIR);
            server.setExecutor(threadPoolExecutor); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createDirIfNotExists(String directoryName) {
        File theDir = new File(directoryName);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + directoryName);
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
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
