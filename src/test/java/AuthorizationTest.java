import fel.cvut.cz.access.Authorization;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * Created by marek on 26.5.16.
 */
public class AuthorizationTest {

    private final String hashedPassword = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";

    @Test
    public void test() {
        Authorization authorization = new Authorization("Basic dXNlcjpwYXNzd29yZA==");

        assertTrue(authorization.getUsername().equals("user"));
        assertTrue(authorization.getPassword().equals("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"));
        assertTrue(authorization.getLineFormat().equals(String.format("user:%s", hashedPassword)));
    }


}
