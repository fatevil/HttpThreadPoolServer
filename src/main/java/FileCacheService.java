import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marek on 22.5.16.
 */
public class FileCacheService {
    private static FileCacheService ourInstance = new FileCacheService();

    public Map<String, SoftReference<File>> cache = new HashMap<>();

    private FileCacheService() {
    }

    public static FileCacheService getInstance() {
        return ourInstance;
    }

    public File getFile(String fullFileName) {
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

    public void removeFile(String fullFileName) {
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

    public boolean fileExists(String fullFileName) {
        if (cache.containsKey(fullFileName)) {
            System.out.printf("Heya! File %s has been found!%n", fullFileName);
            return true;
        } else {
            File f = new File(fullFileName);
            if (f.exists() && !f.isDirectory()) {
                System.out.printf("Heya! File %s has been found!%n", fullFileName);
                return true;
            }
        }
        System.out.printf("Unfortunately file %s has not been found or it is a directory!%n", fullFileName);
        return false;
    }
}
