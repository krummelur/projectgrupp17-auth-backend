package Controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtils {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE),
            VALID_PASSWORD_REGEX = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(^.{6,}$)");

    public static boolean validEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public static boolean validUsername(String emailStr) {
        return true;
    }

    public static boolean validPassword(String passwordStr) {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(passwordStr);
        return matcher.find();
    }
}
