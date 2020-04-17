package Controller;

import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.util.Pair;

/**
 * AuthService interface
 */
public interface IAuthService {
    boolean validateUserPassword(String email, String password);
    Pair<String, String> generateAndSaveTokens(String email);
    boolean deleteRefreshToken(String id);

    String refresh(String access, String refresh) throws ValidationError;
}
