package fel.cvut.cz.access;

import fel.cvut.cz.utils.HashGenerator;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by marek on 26.5.16.
 */
public class Authorization {

    private final String base64;
    private String username;
    private String password; // hashed

    public Authorization(String base64) {
        this.password = null;
        this.username = null;
        this.base64 = base64;
    }

    private void decodeBase64() {
        // Decode data on other side, by processing encoded data
        byte[] valueDecoded = Base64.decodeBase64((base64.substring(5)));
        String decodedString = new String(valueDecoded);
        int index = decodedString.indexOf(":");
        this.username = decodedString.substring(0, index);
        this.password = HashGenerator.createHash(decodedString.substring(index + 1));
    }

    public String getPassword() {
        if (username == null) {
            decodeBase64();
        }
        return password;
    }

    public String getUsername() {
        if (username == null) {
            decodeBase64();
        }
        return username;
    }

    public String getLineFormat() {
        if (username == null) {
            decodeBase64();
        }
        return String.format("%s:%s", username, password);
    }

}
