package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.endpoint.AuthController;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.LoginContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Only tests structure of LoginCredentials. Validation of logins is handled by UserService.
 */
public class LoginCredentialsTest {
    private final String vPassword =  "Password1";
    private final String vEmail = "123";

    @Test
    public void email_may_not_be_null() {
        assertThrows(ValidationError.class, () -> new AuthController.AuthCredentials(null, vPassword).validate());
    }

    @Test
    public void password_may_not_be_null() {
        assertThrows(ValidationError.class, () -> new AuthController.AuthCredentials(vEmail, null).validate());
    }
}
