package fel.cvut.cz.access;

import fel.cvut.cz.utils.HashGenerator;
import org.apache.commons.codec.binary.Base64;

import java.util.logging.Logger;

/**
 * Keeps user authentication. Doesn't keep raw password, only encoded string.
 * <p>
 * Created by marek on 26.5.16.
 */
public class Authorization {
    private static final Logger logger = Logger.getLogger(Authorization.class.getName());

    private final String base64;
    private String username;
    private String password; // hashed

    /**
     * Default constructor accepting base64 string. It is encoded only if it's used.
     *
     * @param base64 encoded string
     */
    public Authorization(String base64) {
        this.password = null;
        this.username = null;
        this.base64 = base64;
    }

    private void decodeBase64() {
        logger.info("Decoding base64.");
        // Decode data on other side, by processing encoded data
        if (base64.length() <= 5) {
            this.username = "";
            this.password = "";
            logger.info("Decoding unsuccessful.");
            return;
        }
        byte[] valueDecoded = Base64.decodeBase64((base64.substring(5)));
        String decodedString = new String(valueDecoded);
        int index = decodedString.indexOf(":");
        this.username = decodedString.substring(0, index);
        this.password = HashGenerator.createHash(decodedString.substring(index + 1));
        logger.info("Decoding successful for user '" + username + "'.");
    }

    /**
     * If it has not been already done, it decodes base64 and returns hashed password.
     *
     * @return hashed password
     */
    public String getPassword() {
        if (username == null) {
            decodeBase64();
        }
        return password;
    }

    /**
     * If it has not been already done, it decodes base64 and returns username.
     */
    public String getUsername() {
        if (username == null) {
            decodeBase64();
        }
        return username;
    }

    /**
     * If it has not been already done, it decodes base64 and returns string that looks like 'user:hashed_password'.
     *
     * @return string required in .htaccess file
     */
    public String getLineFormat() {
        if (username == null) {
            decodeBase64();
        }
        return String.format("%s:%s", username, password);
    }

}
