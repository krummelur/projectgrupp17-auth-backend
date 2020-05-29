package com.fredriksonsound.iot_backoffice_auth.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for users
 */
public class UserUtils {
    private UserUtils() {}
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE),
            VALID_PASSWORD_REGEX = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(^.{6,32}$)"),
            VALID_USERNAME_REGEX = Pattern.compile("^[a-z0-9]{4,32}$", Pattern.CASE_INSENSITIVE);

    public static boolean validEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    /**
     * Verifies that an email address is a valid email address
     * @param usernameStr the email address
     * @return ok if valid
     */
    public static boolean validUsername(String usernameStr) {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(usernameStr);
        return matcher.find();
    }


    /**
     * Verifies that an password is valid
     * RULES:
     * minlen = 6
     * maxlen = 32
     * must have ONE upper case char
     * must have 1 number
     *
     * @param passwordStr the password
     * @return ok if valid
     */
    public static boolean validPassword(String passwordStr) {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(passwordStr);
        return matcher.find();
    }
}
