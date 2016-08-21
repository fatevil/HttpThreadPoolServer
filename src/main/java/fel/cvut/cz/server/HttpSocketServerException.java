package fel.cvut.cz.server;

/**
 * Exception to be thrown when internal errors occur.
 * <p>
 * Created by marek on 27.5.16.
 */
public class HttpSocketServerException extends Exception {
    public HttpSocketServerException(String message) {
        super(message);
    }
}
