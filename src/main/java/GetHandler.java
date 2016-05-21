import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Created by marek on 21.5.16.
 */
public class GetHandler implements HttpHandler {

    static final String dir = "C:/temp";


    @Override
    public void handle(HttpExchange t) throws IOException {

    }
}
