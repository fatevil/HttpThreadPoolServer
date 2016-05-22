import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.HashGenerator;
import utils.RestrictedAccessException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Created by marek on 21.5.16.
 */
public abstract class AbstractHttpHandler implements HttpHandler {

    public static boolean checkPermission(HttpExchange t) throws RestrictedAccessException {
        String uri = t.getRequestURI().toString();
        String filename = String.format("%s%s", Server.FILES_DIR, uri.substring(0, uri.lastIndexOf("/")).concat("/.htaccess"));
        if (!FileCacheService.getInstance().fileExists(filename)) {
            return false;
        }

        Headers var2 = t.getRequestHeaders();
        String var3 = var2.getFirst("Authorization");
        if (var3 == null) {
            throw new RestrictedAccessException("Authorization header is missing!");
        } else {
            int var4 = var3.indexOf(32);
            if (var4 != -1 && var3.substring(0, var4).equals("Basic")) {

                byte[] var5 = Base64.getDecoder().decode(var3.substring(var4 + 1));
                String var6 = new String(var5);
                int var7 = var6.indexOf(58);
                String var8 = var6.substring(0, var7); //username
                String var9 = var6.substring(var7 + 1);   //password


                try {
                    if (Files.readAllLines(Paths.get(filename)).stream().anyMatch(s -> {
                        int index = s.indexOf(":");

                        String[] array = {s.substring(0, index), s.substring(index + 1)};

                        if (var8.equals(array[0]) && HashGenerator.createHash(var9).equals(array[1])) {
                            return true;
                        } else {
                            return false;
                        }
                    })) {
                        return true;
                    } else {
                        throw new RestrictedAccessException("This user is not permitted to access this folder!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
            }
            throw new RestrictedAccessException("Wrong encoding!");
        }
    }

    public void sendResponseAndClose(int code, String message, HttpExchange t) {
        try (OutputStream os = t.getResponseBody()) {
            t.sendResponseHeaders(code, message.length());
            os.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataAndClose(int code, byte[] bytearray, long lengthOfOutputFile, HttpExchange t) {
        try (OutputStream os = t.getResponseBody()) {
            t.sendResponseHeaders(200, lengthOfOutputFile);
            os.write(bytearray, 0, bytearray.length);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
