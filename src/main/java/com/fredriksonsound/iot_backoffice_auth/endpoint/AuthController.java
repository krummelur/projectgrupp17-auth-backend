package com.fredriksonsound.iot_backoffice_auth.endpoint;

import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.web.ErrorResponse;
import com.google.gson.JsonObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class AuthController {

    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> loginWithCredentials(@RequestBody AuthCredentials credentials, UriComponentsBuilder ucBuilder) {
        try { credentials.validate(); } catch (ValidationError v) {
            return ErrorResponse.JsonFromMessage("invalid credentials").collect();
        }

        var gson = new JsonObject();
        gson.addProperty("Result", "OKAYYY");
        return new ResponseEntity<>(gson, new HttpHeaders(), HttpStatus.OK);
    }

    class AuthCredentials implements ValidationError.Validatable {
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

