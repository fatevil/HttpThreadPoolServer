package fel.cvut.cz.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for creating folders and giving restricted access to it.
 * <p>
 * Created by marek on 22.5.16.
 */
public class CustomFileUtils {
    private static final Logger logger = Logger.getLogger(CustomFileUtils.class.getName());

    /**
     * Creates directory if it doesn't exist yet.
     *
     * @param directoryName created folder
     */
    public static void createDirIfNotExists(String directoryName) {
        File theDir = new File(directoryName);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            logger.info("Creating directory: '" + directoryName + "'.");
            boolean result = false;

            try {

                if (theDir.mkdir()) {
                    result = true;
                }
            } catch (SecurityException se) {
                //handle it
                logger.log(Level.WARNING, "Security exception during directory creation", se);
            }
            if (result) {
                logger.info("Directory: '" + directoryName + "' created.");
            }
        }
    }

    /**
     * Creates .htaccess file with testing user "user:password".
     *
     * @param directory restricted folder
     */
    public static void putHtaccessToDir(String directory) {
        Path file = Paths.get(String.format("%s/.htaccess", directory));
        if (Files.exists(file)) {
            return;
        }

        List<String> lines = Arrays.asList("user:8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
            logger.info(String.format("Created %s in %s%n", file.getFileName(), directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
