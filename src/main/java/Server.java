import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by marek on 21.5.16.
 */
public class Server implements Runnable {

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

            server.createContext("/", new RequestHandler());

            ExecutorService threadPoolExecutor =
                    new ThreadPoolExecutor(
                            corePoolSize,
                            maxPoolSize,
                            keepAliveTime,
                            TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>()
                    );


            createDirIfNotExists("files");
            server.setExecutor(threadPoolExecutor); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createDirIfNotExists(String directoryName) {
        File theDir = new File("new folder");

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
