package fel.cvut.cz.handling;

import fel.cvut.cz.Server;
import fel.cvut.cz.access.Authorization;
import fel.cvut.cz.server.HttpSocketServerException;
import fel.cvut.cz.server.HttpSocketServerRequest;
import fel.cvut.cz.server.HttpSocketServerResponse;

import java.io.*;

/**
 * Created by marek on 26.5.16.
 */
public class HttpExchangeSerivce {

    private final HttpSocketServerRequest request;
    private final HttpSocketServerResponse response;
    private String file;
    private String directory;
    private Authorization authorization;
    private Boolean textNotFile = null;

    public HttpExchangeSerivce(HttpSocketServerRequest request, HttpSocketServerResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Text files are given to dir :web_content. - default for PUT
     * <p>
     * Other files are given to dir: files -  default for other methods
     * <p>
     *
     * @return wheter to put files to dir "files" or "web_content"
     */
    public boolean isTextNotFile() {
        if (textNotFile == null) {
            String responseDataType;
            if (request.getRequestMethod().toString().equals("PUT")) {
                responseDataType = request.getRequestHeaders().getFirst("Content-Type");
                return responseDataType.contains("text");
            } else {
                if (request.getRequestMethod().toString().equals("DELETE")) {
                    responseDataType = request.getRequestHeaders().getFirst("Content-Type");
                    return textNotFile = responseDataType.contains("text");
                } else {
                    responseDataType = request.getRequestHeaders().getFirst("Accept");
                    return (responseDataType.equals("*/*") || responseDataType.contains("text"));
                }
            }
        }

        return textNotFile;
    }

    public String getTargetFile() {
        if (file == null) {
            if (isTextNotFile()) {
                if (request.getRequestURI().toString().equals("/")) {
                    this.file = Server.CONTENT_DIR + "/index.html";
                } else {
                    this.file = Server.CONTENT_DIR + request.getRequestURI().toString();
                }
            } else {
                this.file = Server.FILES_DIR + request.getRequestURI().toString();
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
        if (request.getRequestHeaders().containsKey("Authorization")) {
            authorization = new Authorization(request.getRequestHeaders().getFirst("Authorization"));
        }
        return authorization;
    }

    public void sendTextResponseAndClose(int code, String message) {
        sendResponseAndClose(code, message.getBytes(), message.length());
    }

    public void sendResponseAndClose(int code, byte[] bytearray, long lengthOfOutputFile) {
        try (OutputStream os = response.getResponseBody()) {
            response.sendResponseHeaders(code, lengthOfOutputFile);
            os.write(bytearray, 0, bytearray.length);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpSocketServerException e) {
            e.printStackTrace();
        }
    }

    public void addResponseHeader(String key, String value) {
        response.getResponseHeaders().add(key, value);
    }


    public void saveFileFromRequestHeader(File destinationFile) {
        int i;
        InputStream input;
        input = request.getRequestBody();
        BufferedInputStream in =
                new BufferedInputStream(input);

        String fullFileName = getTargetFile();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in)); FileWriter out = new FileWriter(destinationFile)) {
            while ((i = reader.read()) != -1) {
                out.write(i);
            }
            System.out.printf("File %s saved!%n", fullFileName);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.format("File %s not saved!", fullFileName));
        }


    }
}
