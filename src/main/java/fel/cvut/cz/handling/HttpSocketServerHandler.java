package fel.cvut.cz.handling;

import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

/**
 * Created by marek on 27.5.16.
 */
public interface HttpSocketServerHandler {

    void handle(HttpSocketServerRequest httpSocketServerRequest, HttpSocketServerResponse httpSocketServerResponse);

}
