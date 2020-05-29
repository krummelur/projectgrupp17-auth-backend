package com.fredriksonsound.iot_backoffice_auth.service;

import com.fredriksonsound.iot_backoffice_auth.data.TokenRepository;
import com.fredriksonsound.iot_backoffice_auth.data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.model.RefreshToken;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.util.PasswordUtils;
import com.fredriksonsound.iot_backoffice_auth.util.Pair;
import com.fredriksonsound.iot_backoffice_auth.util.TokensUtils;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * AuthService implementation
 */
@Component
@Service
public class AuthService implements IAuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    /**
     * Checks that a given password matches a given user email
     * @param email the users email
     * @param password the password
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
     * @param email the email of the user
     * @return and access token and a refresh token id.
     */
    @Override
    public Pair<String, String> generateAndSaveTokens(String email) {
        var tokenId = UUID.randomUUID().toString();
        var refreshTokenId = UUID.randomUUID().toString();
        Pair<String, String> tokens =
                new Pair (TokensUtils.getAccessToken(tokenId, email.toLowerCase()),
                          TokensUtils.getRefreshToken(refreshTokenId, email.toLowerCase()));

        RefreshToken refreshToken = new RefreshToken(refreshTokenId, tokens.second);
        tokenRepository.save(refreshToken);
        return new Pair(tokens.first, refreshTokenId);
    }

    /**
     * Deletes a specified access token by id
      * @param id the access token identifier
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
     * Validates whether a tokens access-level (authToken.subject) is valid for accessing the resource (subject)
     * @param accessToken the accesstoken provided
     * @param subject the subject to match
     * @return true if match and valid token
     * @throws ValidationError on invalid or expired token or nonmatching user
     */
    @Override
    public boolean validateAccessFor(String accessToken, String subject) throws ValidationError {
        DefaultClaims token = null;
        try
            { token = (DefaultClaims) (TokensUtils.decodeJwToken(accessToken).getBody()); }
        catch (MalformedJwtException | SignatureException | IllegalArgumentException e)
            { throw new ValidationError(ERROR_CODE.INVALID_JWT);}

        if((Integer)token.get("exp") < System.currentTimeMillis()/1000)
            throw new ValidationError(ERROR_CODE.EXPIRED_ACCESS_TOKEN);

        if (token.getSubject().equals(subject) || token.getSubject().equals("admin"))
            return true;
        throw new ValidationError(ERROR_CODE.UNAUTHORIZED_RESOURCE_ACCESS);
    }

    /**
     * Generates a new access token given an expired accesstoken and a refresh token id
     * @param access the expired accessToken to refresh
     * @param refreshId the matching refreshtoken identifier
     * @return a new access token
     * @throws ValidationError if invalid parameters
     */
    @Override
    public String refresh(String access, String refreshId) throws ValidationError {
        System.out.println("REFRESHID: " + refreshId);
        if(!tokenRepository.existsById(refreshId))
            throw new ValidationError(ERROR_CODE.NONEXISTENT_REFRESH_TOKEN);
        DefaultClaims parsed = null;
        try { parsed = (DefaultClaims) TokensUtils.decodeJwToken(access).getBody();}
        catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            System.err.println("####### SOMEONE TRIED TO USE A TAMPERED WITH TOKEN ####### ERROR:");
            e.printStackTrace();  throw new ValidationError(ERROR_CODE.INVALID_JWT);
        }

        if((Integer)parsed.get("exp") > System.currentTimeMillis()/1000)
            throw new ValidationError(ERROR_CODE.NONEXPIRED_ACCESS_TOKEN);

        RefreshToken refreshTokenInstance = tokenRepository.findById(refreshId).orElseThrow();
        DefaultClaims parsedRefresh = (DefaultClaims) TokensUtils.decodeJwToken(refreshTokenInstance.refresh_token()).getBody();

        if((Integer)parsedRefresh.get("exp") < System.currentTimeMillis()/1000) {
            tokenRepository.deleteById(parsedRefresh.get("jti").toString());
            throw new ValidationError(ERROR_CODE.EXPIRED_REFRESH_TOKEN);
        }

        String newAccessToken = TokensUtils.getAccessToken(UUID.randomUUID().toString(), (String)parsed.get("sub"));
        return newAccessToken;
    }


}
