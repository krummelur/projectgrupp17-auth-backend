package Controller;

import de.rtner.misc.BinTools;
import de.rtner.security.auth.spi.PBKDF2Engine;
import de.rtner.security.auth.spi.PBKDF2Parameters;
import de.rtner.security.auth.spi.SimplePBKDF2;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtils {
    /**
     * Create salted hash from password
     * @param password the passsword to hash
     * @return the string representation of the salted hash
     */
    public static String Hash(String password) {
        return new SimplePBKDF2().deriveKeyFormatted(password);
    }

    /**
     * Verifies that a salted hashed password matches cleartext password
     * @param password
     * @param hash
     * @return
     */
    public static boolean verify(String password, String hash) {
        return  new SimplePBKDF2().verifyKeyFormatted(hash, password);
    }
}
