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
        var h = PasswordUtils.Hash("test");
        var k = PasswordUtils.verify("test", h);
        // Salt 8 bytes SHA1PRNG, HmacSHA1, 1000 iterations, ISO-8859-1
        String s = new SimplePBKDF2().deriveKeyFormatted("password");
// s === "CCD16F76AF3DE30A:1000:B53849A7E20883C77618D3AD16269F98BC4DCA19"
        boolean ok = new SimplePBKDF2().verifyKeyFormatted(s, "password");
        
        return PasswordUtils.verify(password, user.pass_hash());
    }

    public Pair<String, String> generateAndSaveTokens(String email) {
        var tokenId = UUID.randomUUID().toString();
        var refreshTokenId = UUID.randomUUID().toString();
        Pair<String, String> tokens = new Pair (Tokens.getAccessToken(tokenId, email), Tokens.retRefreshToken(refreshTokenId, email));
        Token refreshToken = new Token(refreshTokenId, tokens.second);
        tokenRepository.save(refreshToken);
        return new Pair(tokens.second, refreshTokenId);
    }

    public boolean deleteRefreshToken(String id) {
            if(!tokenRepository.existsById(id))
            return false;
        tokenRepository.deleteById(id);
        return true;
    }
}
