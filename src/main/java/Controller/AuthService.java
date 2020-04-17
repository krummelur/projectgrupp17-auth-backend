package Controller;

import com.fredriksonsound.iot_backoffice_auth.Data.TokenRepository;
import com.fredriksonsound.iot_backoffice_auth.Data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.model.Token;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.util.Pair;
import de.rtner.security.auth.spi.SimplePBKDF2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    public boolean validateUserPassword(String email, String password) {
        User user;
        try {
            user = userRepository.findById(email).get();
        } catch (NoSuchElementException e) {
            return false;
        }
        return PasswordUtils.verify(password, user.pass_hash());
    }

    public Pair<String, String> generateAndSaveTokens(String email) {
        var tokenId = UUID.randomUUID().toString();
        var refreshTokenId = UUID.randomUUID().toString();
        Pair<String, String> tokens = new Pair (Tokens.getAccessToken(tokenId, email), Tokens.retRefreshToken(refreshTokenId, email));
        Token refreshToken = new Token(refreshTokenId, tokens.second);
        tokenRepository.save(refreshToken);
        return new Pair(tokens.first, refreshTokenId);
    }

    public boolean deleteRefreshToken(String id) {
            if(!tokenRepository.existsById(id))
            return false;
        tokenRepository.deleteById(id);
        return true;
    }
}
