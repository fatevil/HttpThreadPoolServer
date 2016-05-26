package fel.cvut.cz.access;

import fel.cvut.cz.utils.HashGenerator;

import java.util.Base64;

/**
 * Created by marek on 26.5.16.
 */
public class Authorization {

    private String username;

    private String password; // hashed

    private final String base64;

    public Authorization(String base64) {
        this.password = null;
        this.username = null;
        this.base64 = base64;
    }

    private void decodeBase64() {
        String base = new String(Base64.getDecoder().decode(base64));
        int splitIndex = base.indexOf(58);
        this.username = base.substring(0, splitIndex); //username
        this.password = HashGenerator.createHash(base.substring(splitIndex + 1));
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
