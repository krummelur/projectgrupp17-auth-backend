package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.model.Credentials;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.fredriksonsound.iot_backoffice_auth.ErrorResponse.*;


@RestController
public class AuthController {
    @Autowired
    private Gson gson;

    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> loginWithCredentials(@RequestBody Credentials credentials, UriComponentsBuilder ucBuilder) {
        try { credentials.validate(); } catch (ValidationError v) {
            return ErrorResponse_json("invalid credentials").collect();
        }

        var gson = new JsonObject();
        gson.addProperty("Result", "OKAYYY");
        return new ResponseEntity<>(gson, new HttpHeaders(), HttpStatus.OK);
    }
}

