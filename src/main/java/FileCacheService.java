import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import utils.HashGenerator;
import utils.RestrictedAccessException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
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

    public boolean checkPermission(HttpExchange t) throws RestrictedAccessException {
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

                String uri = t.getRequestURI().toString();
                try {
                    if (Files.readAllLines(Paths.get(uri.substring(0, uri.lastIndexOf("/")).concat(".htaccess"))).stream().anyMatch(s -> {
                        int index = s.indexOf(":");
                        String[] array = {s.substring(0, index), s.substring(index)};

                        System.out.println("array1 : " + array[0]);
                        System.out.println("array2 : " + array[1]);

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
                    return true;
                }
            }
            throw new RestrictedAccessException("Wrong encoding!");
        }
    }
}
