package fel.cvut.cz.utils;

import com.sun.net.httpserver.HttpExchange;
import fel.cvut.cz.Server;
import fel.cvut.cz.access.Authorization;

/**
 * Created by marek on 26.5.16.
 */
public class HttpExchangeSerivce {

    private final HttpExchange httpExchange;
    private String file;
    private String directory;

    private Boolean textNotFile = null;

    public HttpExchangeSerivce(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    public boolean isTextOrFile() {
        if (textNotFile == null) {
            String responseDataType = httpExchange.getRequestHeaders().getFirst("Accept");
            return (responseDataType.equals("*/*") || responseDataType.contains("text"));
        }
        return textNotFile;
    }

    public String getTargetFile() {
        if (file == null) {
            if (textNotFile) {
                this.file = Server.CONTENT_DIR + httpExchange.getRequestURI();
            } else {
                this.file = Server.FILES_DIR + httpExchange.getRequestURI();
            }
        }
        return file;
    }

    public String getTargetDirectory() {
        if (directory == null) {

        }
        return directory;
    }

    public Authorization getAuthorization() {
        if (httpExchange.getRequestHeaders().containsKey("Authorization")) {
            return new Authorization(httpExchange.getRequestHeaders().getFirst("Authorization"));
        }
        return null;
    }
}
