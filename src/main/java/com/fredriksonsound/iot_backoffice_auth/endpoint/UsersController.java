package com.fredriksonsound.iot_backoffice_auth.endpoint;

import com.fredriksonsound.iot_backoffice_auth.data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.service.AuthService;
import com.fredriksonsound.iot_backoffice_auth.service.Tokens;
import com.fredriksonsound.iot_backoffice_auth.service.UserService;
import com.fredriksonsound.iot_backoffice_auth.service.ERROR_CODE;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.web.ErrorResponse;
import com.fredriksonsound.iot_backoffice_auth.web.OkResponse;
import com.fredriksonsound.iot_backoffice_auth.web.UnauthorizedResponse;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
public class UsersController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AuthService authService;

    /**
     * Creates a new user given user details
     * @param authToken valid auth-token for user
     * @param userEmail the email to look up user details on
     * @return Created on success, error on invalid user
     */
    @CrossOrigin(origins ="*", allowedHeaders="*")
    @RequestMapping(value = "/users/{userEmail}", method = RequestMethod.GET)
    public ResponseEntity<JsonObject> getUSer(@RequestHeader(value = "Auth-Token", required = false) String authToken, @PathVariable("userEmail") String userEmail) {
        if(authToken == null)
            return ErrorResponse.JsonFromMessage("missing Auth-Token header").collect();

        try { authService.validateAccessFor(authToken, userEmail); }
        catch (ValidationError e) {
            switch (e.errorCode) {
                case UNAUTHORIZED_RESOURCE_ACCESS: return UnauthorizedResponse.JsonFromMessage("access token does not grant access to that resource").collect();
                case EXPIRED_ACCESS_TOKEN:   return UnauthorizedResponse.JsonFromMessage("expired access token").collect();
                case INVALID_JWT: return ErrorResponse.JsonFromMessage("JWT could not be parsed").collect();
                default: return ErrorResponse.JsonFromMessage("request failed for unknown reason").collect();
            }
        }

        try {
            var user = userRepo.findById(userEmail).orElseThrow();
            var responseJson = new JsonObject();
            var userObject = new JsonObject();
            userObject.addProperty("email", user.email());
            userObject.addProperty("agency", user.agency());
            userObject.addProperty("username", user.username());
            responseJson.addProperty("status", "success");
            responseJson.add("user", userObject);
            System.out.println(responseJson);
            return new OkResponse<>(responseJson).collect();
        } catch (NoSuchElementException e) {
            var responseJson = new JsonObject();
            responseJson.addProperty("status", "error");
            responseJson.addProperty("message", "no user with such email found");
            return new ErrorResponse<>(responseJson, HttpStatus.NOT_FOUND).collect();
        }
    }
}
