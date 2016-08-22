package fel.cvut.cz;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This singleton class is supposed to be used whenever is required work with files. It keeps files cached so repeatedly used files are directly returned and not searched for on HDD.
 * <p>
 * Created by marek on 22.5.16.
 */
public class FileCacheService {
    private static final Logger logger = Logger.getLogger(FileCacheService.class.getName());

    private static final FileCacheService ourInstance = new FileCacheService();
    private final Map<String, SoftReference<File>> cache = new HashMap<>();

    private FileCacheService() {
    }

    /**
     * Gets the only instance of this class.
     */
    public static FileCacheService getInstance() {
        return ourInstance;
    }

    /**
     * Gets File object from specified location. If the file is already in cache, return it, or add it and then return.
     *
     * @param fullFileName name of the requested file
     * @return requested file
     */
    public File get(String fullFileName) {
        File file = null;
        if (cache.containsKey(fullFileName)) {
            file = cache.get(fullFileName).get();
        }
        if (file != null) {
            return file;
        } else {
            file = new File(fullFileName);
            SoftReference<File> fsr = new SoftReference<>(file);
            cache.put(fullFileName, fsr);
            return file;
        }
    }

    /**
     * Remove file from cache.
     *
     * @param fullFileName full name of removed file
     */
    public void remove(String fullFileName) {
        cache.remove(fullFileName);
    }

    public File createFile(String fullFileName) {
        File file = new File(fullFileName);
        try {
            if (!file.isFile() && !file.createNewFile()) {
                throw new IOException("Error creating new file: " + file.getAbsolutePath());
            }
            SoftReference<File> fsr = new SoftReference<>(file);
            cache.put(fullFileName, fsr);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Figures out if the specified file exists. At first looks to cache, then to its location on disc.
     *
     * @param fullFileName full name of requested file
     * @return true if it exists on disc, false otherwise
     */
    public boolean exists(String fullFileName) {
        if (cache.containsKey(fullFileName)) {
            logger.fine(String.format("%s found!%n", fullFileName));
            return true;
        } else {
            File f = new File(fullFileName);
            if (f.exists()) {
                logger.fine(String.format("%s found!%n", fullFileName));
                return true;
            }
        }
        logger.fine(String.format("%s not found!%n", fullFileName));
        return false;
    }
}
