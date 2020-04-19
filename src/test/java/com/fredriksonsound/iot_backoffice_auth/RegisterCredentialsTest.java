package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Only tests structure of RegisteCredentials. Validation of Registrations is handled by UserService.
 */
public class RegisterCredentialsTest {

    private final String vUser = "magnus";
    private final String vPassword =  "Password1";
    private final String vAgency = "123";
    private final String vEmail = "123";

    @Test
    public void email_may_not_be_null() {
        assertThrows(ValidationError.class, () -> new RegisterController.RegisterCredentials(vUser, vPassword,null,vAgency).validate());
    }

    @Test
    public void password_may_not_be_null() {
        assertThrows(ValidationError.class, () -> new RegisterController.RegisterCredentials(vUser, null, vPassword,vAgency).validate());
    }

    @Test
    public void username_may_not_be_null() {
        assertThrows(ValidationError.class, () -> new RegisterController.RegisterCredentials(null, vPassword,vEmail,vAgency).validate());
    }

    @Test
    public void agency_may_not_be_null() {
        assertThrows(ValidationError.class, () -> new RegisterController.RegisterCredentials(vUser, vPassword,vEmail, null).validate());
    }
}
