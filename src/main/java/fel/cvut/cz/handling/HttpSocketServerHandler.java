package fel.cvut.cz.handling;

import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

/**
 * Implementation of this class is supposed to respond to HTTP requests.
 * <p>
 * Created by marek on 27.5.16.
 */
public interface HttpSocketServerHandler {

    /**
     * Solves HTTP request and makes response.
     *
     * @param httpSocketServerRequest  request specifics
     * @param httpSocketServerResponse response mediator
     */
    void handle(HttpSocketServerRequest httpSocketServerRequest, HttpSocketServerResponse httpSocketServerResponse);

}
