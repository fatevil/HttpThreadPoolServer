import fel.cvut.cz.Server;
import fel.cvut.cz.utils.CustomFileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by marek on 22.5.16.
 */
public class AbstractTest {


    protected static Server server;

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {

        //TODO:tests wont work when they're all instantiated at once, it must be one by one (classes)

        server = new Server();

        ExecutorService service = Executors.newFixedThreadPool(100);
        service.submit(server);


        Thread.sleep(1000);
        CustomFileUtils.createDirIfNotExists(String.format("%s/forbidden_folder_test", Server.FILES_DIR));
        CustomFileUtils.putHtaccessToDir(String.format("%s/forbidden_folder_test", Server.FILES_DIR));
    }

    @AfterClass
    public static void tearDown() {
        server.terminate();
        try {
            Files.deleteIfExists(Paths.get("forbidden_folder_test/tested_file.txt"));
            Files.deleteIfExists(Paths.get("tested_file.txt"));
            Files.deleteIfExists(Paths.get("file_to_be_tested.txt"));
            Files.deleteIfExists(Paths.get("test_file_to_be_deleted.txt"));
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test/.htaccess", Server.FILES_DIR)));
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test/tested_file.txt", Server.FILES_DIR)));
            Files.deleteIfExists(Paths.get(String.format("%s/forbidden_folder_test", Server.FILES_DIR)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void createTestingFile(String name) {
        Path file = Paths.get(name);
        if (Files.exists(file)) {
            return;
        }

        List<String> lines = Arrays.asList("Hi there! This is a test file and it should be retrieved by remote client!");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.printf("Created %s \n", name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
