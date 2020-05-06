package com.fredriksonsound.iot_backoffice_auth.service;

import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.util.Pair;

/**
 * AuthService interface
 */
public interface IAuthService {
    boolean validateUserPassword(String email, String password);
    Pair<String, String> generateAndSaveTokens(String email);
    boolean deleteRefreshToken(String id);
    boolean validateAccessFor(String accessToken, String subject) throws ValidationError;

    String refresh(String access, String refresh) throws ValidationError;
}
