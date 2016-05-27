package fel.cvut.cz.server;

import fel.cvut.cz.handling.HttpSocketServerHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by marek on 27.5.16.
 */
public class HttpSocketServer implements Runnable {

    private final int port;
    private boolean isRunning = false;

    private Logger logger = Logger.getLogger("HttpSocketServer");
    private ExecutorService executorService;
    private HttpSocketServerHandler handler;

    private ServerSocket socket;

    public HttpSocketServer() {
        this(8000);
    }

    public HttpSocketServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket();

            logger.info("Starting HttpSocketServer at http://127.0.0.1:" + port);

            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));

            while (isRunning) {
                Socket connection = null;

                connection = socket.accept();


                HttpSocketServerRequest request = new HttpSocketServerRequest(getHandler(), connection);
                getExecutorService().execute(request);

                logger.info(String.format(
                        "Http request from %s:%d", connection.getInetAddress(), connection.getPort()));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpSocketServerException e) {
            e.printStackTrace();
        }
    }

    public HttpSocketServerHandler getHandler() throws HttpSocketServerException {
        if (handler == null) {
            throw new HttpSocketServerException("Handler not specified!");
        }
        return handler;
    }

    public void setHandler(HttpSocketServerHandler httpHandler) {
        this.handler = httpHandler;
    }

    public void setExecutor(ExecutorService executorService) throws HttpSocketServerException {
        if (isRunning) {
            throw new HttpSocketServerException("Server is already running, you cannot set executor service!");
        }
        this.executorService = executorService;
    }

    public ExecutorService getExecutorService() {
        if (executorService == null) {
            this.executorService = Executors.newFixedThreadPool(100);
        }
        return executorService;
    }

    public void stop() {
        this.isRunning = false;
        executorService.shutdown();
        logger.info("Server has been shutdown!");
    }

    public void start() {
        this.isRunning = true;
        run();
    }
}
