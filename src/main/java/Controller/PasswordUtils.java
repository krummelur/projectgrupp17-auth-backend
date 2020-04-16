package Controller;

import de.rtner.misc.BinTools;
import de.rtner.security.auth.spi.PBKDF2Engine;
import de.rtner.security.auth.spi.PBKDF2Parameters;
import de.rtner.security.auth.spi.SimplePBKDF2;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtils {
    public static String Hash(String password) {
        return new SimplePBKDF2().deriveKeyFormatted(password);
    }
    public static boolean verify(String password, String hash) {
        return  new SimplePBKDF2().verifyKeyFormatted(hash, password);
    }
}
