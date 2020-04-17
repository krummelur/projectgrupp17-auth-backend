package com.fredriksonsound.iot_backoffice_auth.endpoint;

import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.web.CreatedResponse;
import com.fredriksonsound.iot_backoffice_auth.web.ErrorResponse;
import com.fredriksonsound.iot_backoffice_auth.web.OkResponse;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class Default {
    @RequestMapping(value = "/auth/login", method = RequestMethod.GET)
    public ResponseEntity<String> loginWithCredentials(@RequestBody(required = false) AuthController.AuthCredentials credentials) {
        return new OkResponse<String>("auth server v0.0.1").collect();
    }
}
