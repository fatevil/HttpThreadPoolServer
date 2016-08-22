package fel.cvut.cz.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for creating hashed strings.
 * <p>
 * Created by marek on 22.5.16.
 */
public class HashGenerator {
    /**
     * Creates hash from given string.
     *
     * @param s original string
     * @return hashed string
     */
    public static String createHash(String s) {
        String password = "123456";
        StringBuilder sb;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            //convert the byte to hex format method 1

            sb = new StringBuilder();
            for (byte aByteData1 : byteData) {
                sb.append(Integer.toString((aByteData1 & 0xff) + 0x100, 16).substring(1));
            }

            //convert the byte to hex format method 2
            StringBuilder hexString = new StringBuilder();
            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
