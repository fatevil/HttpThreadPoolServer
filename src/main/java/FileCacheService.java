import java.io.File;
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

    public File getFile(String filename) {
        File file = cache.get(filename).get();
        if (file != null) {
            return file;
        } else {
            file = new File(filename);
            SoftReference<File> fsr = new SoftReference<File>(file);
            cache.put(filename, fsr);
            return file;
        }
    }

    public void removeFile(String filename) {
        cache.remove(filename);
    }
}
