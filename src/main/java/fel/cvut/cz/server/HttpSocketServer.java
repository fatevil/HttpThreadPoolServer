package fel.cvut.cz.server;

import fel.cvut.cz.handling.HttpSocketServerHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server listening on specified port. Creates HttpServerRequest from incoming message and provides it to given handler.
 * Allows specified executor to deal with connections or uses fixed thread pool of size 50.
 * <p>
 * Created by marek on 27.5.16.
 */
public class HttpSocketServer implements Runnable {

    private static final Logger logger = Logger.getLogger(HttpSocketServer.class.getName());
    private final int port;
    private boolean isRunning = false;
    private ExecutorService executorService;
    private HttpSocketServerHandler handler;


    /*
     * Default constructor specifying port to start server on.
     */
    public HttpSocketServer(int port) {
        this.port = port;
    }

    /**
     * Opens socket on given port, accepts connections and makes an executor solve its client requests.
     */
    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket();

            logger.info("Listening at http://127.0.0.1:" + port);

            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));

            while (isRunning) {
                Socket connection = socket.accept();

                HttpSocketServerRequest request = new HttpSocketServerRequest(getHandler(), connection);
                getExecutorService().execute(request);

                logger.info(String.format("Executing task from %s:%d", connection.getInetAddress(), connection.getPort()));
            }
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Socket failure!", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO exception!", e);
        } catch (HttpSocketServerException e) {
            logger.log(Level.SEVERE, "HttpSocketServer exception!", e);
        }
    }

    /*
     * Gets the request handler.
     */
    public HttpSocketServerHandler getHandler() throws HttpSocketServerException {
        if (handler == null) {
            HttpSocketServerException exception = new HttpSocketServerException("No handler specified for HttpSocketServer!");
            logger.log(Level.SEVERE, "", exception);
            throw exception;
        }
        return handler;
    }

    /*
     * Registers class responsible for solving client requests and responding to them.
     */
    public void setHandler(HttpSocketServerHandler httpHandler) {
        this.handler = httpHandler;
    }

    /*
     * Registers class responsible for assigning threads to incoming connections.
     */
    public void setExecutor(ExecutorService executorService) throws HttpSocketServerException {
        if (isRunning) {
            HttpSocketServerException exception = new HttpSocketServerException("Server is already running, you cannot set executor service!");
            logger.log(Level.SEVERE, "", exception);
            throw exception;
        }
        this.executorService = executorService;
    }

    /*
     * Gets class responsible for assigning threads to incoming connections.
     */
    public ExecutorService getExecutorService() {
        if (executorService == null) {
            this.executorService = Executors.newFixedThreadPool(500);
        }
        return executorService;
    }

    /*
     * Stops accepting client requests and immediately terminates executing tasks assigned by clients.
     */
    public void stop() {
        this.isRunning = false;
        executorService.shutdown();
        logger.info("Server has been shutdown!");
    }

    /*
     * Start listening and executing tasks.
     */
    public void start() {
        this.isRunning = true;
        run();
    }
}
