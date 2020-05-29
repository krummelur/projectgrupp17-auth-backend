/**
 * @author Magnus Fredriksson
 * This class handles the bindings for the public /users interface
 */

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

/**
 * Register endpoint
 */
@RestController
public class RegisterController {
    @Autowired
    private UserService userService;

    /**
     * <br><br>
     * <b>  API doc:</b><br>
     * <b>  Description</b>: Creates a new user given user details<br>
     * <b>  Method</b>: POST <br>
     * <b>  Location</b>: /users <br>
     * <b>  Body</b>: <br>
     *     { <br>
     *     <i>
     *          username: [username], <br>
     *          email: [email],<br>
     *          password: [password],<br>
     *          agency: [an_existing_agency]<br>
     *      </i>
     *      } <br>
     *
     * <b>  Success response</b>: {status: "ok"}, CODE: 201 <br>
     * <b>  Error response</b>: {status: "error", message: [error_message]}
     *
     * @param credentials the user to create
     * @return the location of the created user or error, error on invalid user
     *
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
