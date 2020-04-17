package Controller;

import com.fredriksonsound.iot_backoffice_auth.util.Pair;

public interface IAuthService {
    boolean validateUserPassword(String email, String password);
    Pair<String, String> generateAndSaveTokens(String email);
    boolean deleteRefreshToken(String id);
}
