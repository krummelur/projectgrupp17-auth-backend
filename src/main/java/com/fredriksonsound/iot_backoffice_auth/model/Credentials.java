package com.fredriksonsound.iot_backoffice_auth.model;

import org.springframework.http.HttpStatus;

public class Credentials implements Validatable {
    public String username, password, email;

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String email() {
        return email;
    }

    @Override
    public boolean validate() throws ValidationError {
        if (email != null && password != null) { return true; }
        throw new ValidationError("missing credentials");
    }
}
