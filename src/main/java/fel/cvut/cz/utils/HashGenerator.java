package fel.cvut.cz.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for creating hashed strings.
 * <p>
 * Created by marek on 22.5.16.
 */
public class HashGenerator {
    private static final Logger logger = Logger.getLogger(HashGenerator.class.getName());

    /**
     * Creates hash from given string.
     *
     * @param s original string
     * @return hashed string
     */
    public static String createHash(String s) {
        logger.fine("Creating hash for '" + s + "'.");

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
            logger.fine("Hash created.");
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, "Couldn't create hash!", ex);
        }
        logger.log(Level.SEVERE, "Couldn't create hash, return null.");
        return null;
    }
}
