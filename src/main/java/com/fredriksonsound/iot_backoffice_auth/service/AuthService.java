package com.fredriksonsound.iot_backoffice_auth.service;

import com.fredriksonsound.iot_backoffice_auth.data.TokenRepository;
import com.fredriksonsound.iot_backoffice_auth.data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.model.RefreshToken;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.model.util.PasswordUtils;
import com.fredriksonsound.iot_backoffice_auth.util.Pair;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
@Service
public class AuthService implements IAuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    /**
     * Checks that a given password matches a given user email
     * @param email
     * @param password
     * @return true if match, false if not match
     */
    @Override
    public boolean validateUserPassword(String email, String password) {
        User user;
        try {
            user = userRepository.findById(email.toLowerCase()).get();
        } catch (NoSuchElementException e) {
            return false;
        }
        return PasswordUtils.verify(password, user.pass_hash());
    }

    /**
     * Generates a new access token and refresh token.
     * @param email
     * @return and access token and a refresh token id.
     */
    @Override
    public Pair<String, String> generateAndSaveTokens(String email) {
        var tokenId = UUID.randomUUID().toString();
        var refreshTokenId = UUID.randomUUID().toString();
        Pair<String, String> tokens =
                new Pair (Tokens.getAccessToken(tokenId, email.toLowerCase()),
                          Tokens.getRefreshToken(refreshTokenId, email.toLowerCase()));

        RefreshToken refreshToken = new RefreshToken(refreshTokenId, tokens.second);
        tokenRepository.save(refreshToken);
        return new Pair(tokens.first, refreshTokenId);
    }

    /**
     * Deletes a specified access token by id
      * @param id
     * @return true if the deletion was a success
     */
    @Override
    public boolean deleteRefreshToken(String id) {
            if(!tokenRepository.existsById(id))
            return false;
        tokenRepository.deleteById(id);
        return true;
    }

    /**
     * Generates a new access token given an expired accesstoken and a refresh token id
     * @param access
     * @param refreshId
     * @return a new access token
     * @throws ValidationError
     */
    @Override
    public String refresh(String access, String refreshId) throws ValidationError {
        System.out.println("REFRESHID: " + refreshId);
        if(!tokenRepository.existsById(refreshId))
            throw new ValidationError(ERROR_CODE.NONEXISTENT_REFRESH_TOKEN);
        DefaultClaims parsed = null;
        try { parsed = (DefaultClaims) Tokens.decodeJwToken(access).getBody();}
        catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            System.err.println("####### SOMEONE TRIED TO USE A TAMPERED WITH TOKEN ####### ERROR:");
            e.printStackTrace();  throw new ValidationError(ERROR_CODE.INVALID_JWT);
        }

        if((Integer)parsed.get("exp") > System.currentTimeMillis()/1000)
            throw new ValidationError(ERROR_CODE.NONEXPIRED_ACCESS_TOKEN);

        RefreshToken refreshTokenInstance = tokenRepository.findById(refreshId).orElseThrow();
        DefaultClaims parsedRefresh = (DefaultClaims) Tokens.decodeJwToken(refreshTokenInstance.refresh_token()).getBody();

        if((Integer)parsedRefresh.get("exp") < System.currentTimeMillis()/1000) {
            tokenRepository.deleteById(parsedRefresh.get("jti").toString());
            throw new ValidationError(ERROR_CODE.EXPIRED_REFRESH_TOKEN);
        }

        String newAccessToken = Tokens.getAccessToken(UUID.randomUUID().toString(), (String)parsed.get("sub"));
        return newAccessToken;
    }
}
