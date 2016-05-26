import fel.cvut.cz.access.AccessHandler;
import fel.cvut.cz.access.Authorization;
import fel.cvut.cz.utils.CustomFileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static fel.cvut.cz.Server.FILES_DIR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by marek on 26.5.16.
 */
public class AccessHandlerTest {

    private static Authorization authorization;

    @BeforeClass

    public static void setup() {
        CustomFileUtils.createDirIfNotExists(FILES_DIR);
        CustomFileUtils.createDirIfNotExists(String.format("%s/forbidden_folder_test", FILES_DIR));
        CustomFileUtils.putHtaccessToDir(String.format("%s/forbidden_folder_test", FILES_DIR));

        authorization = new Authorization("Basic dXNlcjpwYXNzd29yZA==");
    }

    @AfterClass
    public static void tearDown() {
        new File("forbidden_folder_test/.htaccess").delete();
        new File("tested_file.txt").delete();
    }

    @Test
    public void testBasicNoHtaccess() {
        assertTrue(AccessHandler.check(FILES_DIR, null));
        assertTrue(AccessHandler.check(FILES_DIR, authorization));
    }

    @Test
    public void testHtaccessRestricted() {
        assertFalse(AccessHandler.check(String.format("%s/forbidden_folder_test", FILES_DIR), null));
        assertFalse(AccessHandler.check(String.format("%s/forbidden_folder_test", FILES_DIR), new Authorization("")));
    }

    @Test
    public void testHtaccessSuccess() {
        assertTrue(AccessHandler.check(String.format("%s/forbidden_folder_test", FILES_DIR), authorization));
    }
}
