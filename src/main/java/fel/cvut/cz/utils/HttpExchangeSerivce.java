package fel.cvut.cz.utils;

import com.sun.net.httpserver.HttpExchange;
import fel.cvut.cz.Server;
import fel.cvut.cz.access.Authorization;

import java.io.*;

/**
 * Created by marek on 26.5.16.
 */
public class HttpExchangeSerivce {

    private final HttpExchange httpExchange;
    private String file;
    private String directory;
    private Authorization authorization;
    private Boolean textNotFile = null;

    public HttpExchangeSerivce(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    public boolean isTextOrFile() {
        if (textNotFile == null) {
            String responseDataType = httpExchange.getRequestHeaders().getFirst("Accept");
            return textNotFile = (responseDataType.equals("*/*") || responseDataType.contains("text"));
        }
        return textNotFile;
    }

    public String getTargetFile() {
        if (file == null) {
            if (isTextOrFile()) {
                this.file = Server.CONTENT_DIR + httpExchange.getRequestURI();
            } else {
                this.file = Server.FILES_DIR + httpExchange.getRequestURI();
            }
        }
        return file;
    }

    public String getTargetDirectory() {
        if (directory == null) {
            this.directory = getTargetFile().substring(0, getTargetFile().lastIndexOf("/"));
        }
        return directory;
    }

    public Authorization getAuthorization() {
        if (httpExchange.getRequestHeaders().containsKey("Authorization")) {
            authorization = new Authorization(httpExchange.getRequestHeaders().getFirst("Authorization"));
        }
        return authorization;
    }

    public void sendTextResponseAndClose(int code, String message) {
        sendResponseAndClose(code, message.getBytes(), message.length());
    }

    public void sendResponseAndClose(int code, byte[] bytearray, long lengthOfOutputFile) {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(code, lengthOfOutputFile);
            os.write(bytearray, 0, bytearray.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addResponseHeader(String key, String value) {
        httpExchange.getResponseHeaders().add(key, value);
    }

    public Boolean textNotFile() {
        return textNotFile;
    }

    public void saveFileFromRequestHeader(File destinationFile) {
        int i;
        InputStream input;
        input = httpExchange.getRequestBody();
        BufferedInputStream in =
                new BufferedInputStream(input);

        String fullFileName = getTargetFile();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in)); FileWriter out = new FileWriter(destinationFile)) {
            while ((i = reader.read()) != -1) {
                out.write(i);
            }
            System.out.printf("File %s saved!%n", fullFileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.format("File %s not saved!", fullFileName));
        }

    }
}