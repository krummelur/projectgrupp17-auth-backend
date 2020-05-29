package com.fredriksonsound.iot_backoffice_auth.util;

import de.rtner.security.auth.spi.SimplePBKDF2;
/**
 * Utility class for passwords
 */
public class PasswordUtils {
    private PasswordUtils() {}
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
     * @param password the password to check
     * @param hash the hash to check against
     * @return true if match, else false
     */
    public static boolean verify(String password, String hash) {
        return  new SimplePBKDF2().verifyKeyFormatted(hash, password);
    }
}
