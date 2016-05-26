package fel.cvut.cz.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by marek on 22.5.16.
 */
public class Util {
    public static void createDirIfNotExists(String directoryName) {
        File theDir = new File(directoryName);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + directoryName);
            boolean result = false;

            try {

                if (theDir.mkdir()) {
                    result = true;
                }
            } catch (SecurityException se) {
                //handle it
                se.printStackTrace();
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
    }

    /**
     * Creates .htaccess file with permission given to testing user "user:password".
     *
     * @param directory be locked by .htacces
     */
    public static void putHtaccessToDir(String directory) {
        Path file = Paths.get(String.format("%s/.htaccess", directory));
        if (!Files.exists(file)) {
            return;
        }

        List<String> lines = Arrays.asList("user:8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.printf("Created %s in %s%n", file.getFileName(), directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
