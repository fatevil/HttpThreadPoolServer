import fel.cvut.cz.Server;
import fel.cvut.cz.utils.CustomFileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by marek on 22.5.16.
 */
public class AbstractTest {
    private static final Logger logger = Logger.getLogger(AbstractTest.class.getName());

    protected static Server server;

    @BeforeClass
    public static void setUp() throws IOException, InterruptedException {
        //TODO:tests wont work when they're all instantiated at once, it must be done one by one (classes)

        logger.info("Setting up abstract test class!");
        server = new Server();

        ExecutorService service = Executors.newFixedThreadPool(100);
        service.submit(server);

        Thread.sleep(1000);
        CustomFileUtils.createDirIfNotExists(String.format("%s/forbidden_folder_test", Server.FILES_DIR));
        CustomFileUtils.putHtaccessToDir(String.format("%s/forbidden_folder_test", Server.FILES_DIR));
        logger.info("Abstract test class set up.");
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
            logger.info("Test files deleted!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Test class couldn't delete test files!", e);
        }
    }


}
