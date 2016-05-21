import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
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


            server.setExecutor(threadPoolExecutor); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            switch (t.getRequestMethod()) {
                case "GET":
                    new GetHandler().handle(t);
                    break;
                case "DELETE":
                    new DeleteHandler().handle(t);
                    break;
                case "PUT":
                    new PutHandler().handle(t);
                    break;
            }

            String response = "";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
