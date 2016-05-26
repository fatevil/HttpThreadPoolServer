import fel.cvut.cz.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;

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
public class AbstractTest {


    protected static Server server;

    @BeforeClass
    public static void setUp() throws IOException {
        server = new Server();
        Thread t = new Thread(server);
        server.run();
    }

    @AfterClass
    public static void tearDown() {
        server.terminate();
        new File("forbidden_folder/tested_file.txt").delete();
        new File("tested_file.txt").delete();
    }

    protected void createTestingFile(String name) {
        Path file = Paths.get(String.format("%s/%s", Server.FILES_DIR, name));
        if (Files.exists(file)) {
            return;
        }

        List<String> lines = Arrays.asList("Hi there! This is a test file and it should be retrieved by remote client!");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
            System.out.printf("Created %s in %s%n", name, Server.FILES_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
