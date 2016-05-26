package fel.cvut.cz.access;

import fel.cvut.cz.FileCacheService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Created by marek on 26.5.16.
 */
public class AccesHandler {

    /**
     * @param directory     in which to check .htaccess
     * @param authorization - object containing user:password
     * @return true for approved access
     */
    public static boolean check(String directory, Authorization authorization) {
        String htaccess = directory.concat("/.htaccess");
        if (!FileCacheService.getInstance().exists(htaccess)) {
            return true;
        } else if (authorization == null) {
            System.out.println(authorization.getLineFormat());
            return false;
        }

        try {
            System.out.println(authorization.getLineFormat());
            return (Files.readAllLines(Paths.get(htaccess)).stream().anyMatch(s -> s.equals(authorization.getLineFormat())));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}