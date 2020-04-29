package com.fredriksonsound.iot_backoffice_auth.endpoint;

import com.fredriksonsound.iot_backoffice_auth.service.UserService;
import com.fredriksonsound.iot_backoffice_auth.service.ERROR_CODE;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.web.ErrorResponse;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegisterController {
    @Autowired
    private UserService userService;

    /**
     * Creates a new user given user details
     * @param credentials the user to create
     * @return Created on success, error on invalid user
     */
    @CrossOrigin(origins ="*", allowedHeaders="*")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> registerUser(@RequestBody RegisterCredentials credentials) {

        try { credentials.validate(); } catch (ValidationError v) {
            return ErrorResponse.JsonFromMessage("Invalid body, missing key(s)").collect();
        }

        try { userService.saveNewUser(credentials); } catch (ValidationError e) {
            switch (e.errorCode) {
                case INVALID_EMAIL:
                    return ErrorResponse.JsonFromMessage("invalid email").collect();
                case INVALID_PASSWORD:
                    return ErrorResponse.JsonFromMessage("invalid password").collect();
                case INVALID_USERNAME:
                    return ErrorResponse.JsonFromMessage("invalid username").collect();
                case CONFLICTING_USER:
                    return ErrorResponse.JsonFromMessage("user or email aready taken").collect();
                case NONEXISTENT_AGENCY:
                    return ErrorResponse.JsonFromMessage("agency does not exist").collect();
                default:
                    throw new RuntimeException("Unmapped error code encountered");
            }
        }

        var json = new JsonObject();
        json.addProperty("status", "ok");
        return new ResponseEntity<>(json, new HttpHeaders(), HttpStatus.CREATED);
    }

    public static class RegisterCredentials implements ValidationError.Validatable {
        private String username, password, email, agency;

        public RegisterCredentials(){};

        public RegisterCredentials(String username, String password, String email, String agency) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.agency = agency;
        }

        public String username() {
            return username;
        }
        public String password() {
            return password;
        }
        public String email() {
            return email;
        }

        public String agency() {
            return agency;
        }

        @Override
        public boolean validate() throws ValidationError {
            if (email != null && password != null && username != null && agency != null)
                return true;
            throw new ValidationError(ERROR_CODE.NONE);
        }
    }
}
