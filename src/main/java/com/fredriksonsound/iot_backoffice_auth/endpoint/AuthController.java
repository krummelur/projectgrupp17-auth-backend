package com.fredriksonsound.iot_backoffice_auth.endpoint;

import Controller.AuthService;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.web.CreatedResponse;
import com.fredriksonsound.iot_backoffice_auth.web.ErrorResponse;
import com.fredriksonsound.iot_backoffice_auth.web.OkResponse;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Ref;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> loginWithCredentials(@RequestBody AuthCredentials credentials) {
        try { credentials.validate(); } catch (ValidationError v) {
            return ErrorResponse.JsonFromMessage("invalid credentials").collect();
        }
        if(authService.validateUserPassword(credentials.email, credentials.password)) {
            var tokenPair = authService.generateAndSaveTokens(credentials.email);
            JsonObject data = new JsonObject();
            data.addProperty("token", tokenPair.first);
            data.addProperty("refreshtoken", tokenPair.second);
            return new CreatedResponse<>(data).collect();
        }
        return ErrorResponse.JsonFromMessage("invalid login").collect();
    }

    @RequestMapping(value = "/auth/logout", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> logoutToken(@RequestHeader(value = "Refresh-Token", required = false) String refresh) {
        if(refresh != null && authService.deleteRefreshToken(refresh))
            return OkResponse.JsonFromMessage("deleted").collect();
        return ErrorResponse.JsonFromMessage("no such token").collect();
    }


    static class AuthCredentials implements ValidationError.Validatable {
        String password, email;
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
}

