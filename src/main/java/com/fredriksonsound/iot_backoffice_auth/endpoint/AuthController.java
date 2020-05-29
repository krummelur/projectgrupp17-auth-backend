package com.fredriksonsound.iot_backoffice_auth.endpoint;

import com.fredriksonsound.iot_backoffice_auth.service.AuthService;
import com.fredriksonsound.iot_backoffice_auth.service.ERROR_CODE;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.web.CreatedResponse;
import com.fredriksonsound.iot_backoffice_auth.web.ErrorResponse;
import com.fredriksonsound.iot_backoffice_auth.web.OkResponse;
import com.fredriksonsound.iot_backoffice_auth.web.UnauthorizedResponse;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication api
 */
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * Refreshes an access token using specified refresh token
     * <br><br>
     * <b>  API doc:</b><br>
     * <b>  Description</b>: Refreshes an access token using specified refresh token <br>
     * <b>  Method</b>: POST <br>
     * <b>  Location</b>: /users <br>
     * <b>  Headers</b>:<br>
     * <b>  Refresh-Token</b>: the refresh token to use <br>
     * <b>  Auth-Token</b>: the expired auth token <br><br>
     * <b>  Success response</b>: {status: "success", token: [token]}, CODE: 200 <br>
     * <b>  Error response</b>: {status: "error", message: [error_message]}, 400 or 401
     *
     * @param refresh the refresh token id
     * @param access the JWT access token
     * @return Json with token
     *
     */
    @CrossOrigin(origins ="*", allowedHeaders="*")
    @RequestMapping(value = "/auth/refresh", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> refreshAccessToken(@RequestHeader(value = "Refresh-Token", required = false) String refresh,
                                                         @RequestHeader(value = "Auth-Token", required = false) String access)  {
        if(refresh == null || refresh.equals(""))
            return ErrorResponse.JsonFromMessage("NO refresh token in request").collect();
        if(access == null || access.equals(""))
            return ErrorResponse.JsonFromMessage("NO access token in request").collect();

        String newToken = null;
        try { newToken = authService.refresh(access, refresh); } catch (ValidationError v) {
            switch (v.errorCode) {
                case NONEXISTENT_REFRESH_TOKEN:
                    return UnauthorizedResponse.JsonFromMessage("no such refresh token").collect();
                case INVALID_JWT:
                    return UnauthorizedResponse.JsonFromMessage("invalid jwt").collect();
                case NONEXPIRED_ACCESS_TOKEN:
                    return UnauthorizedResponse.JsonFromMessage("auth token was not expired").collect();
                case EXPIRED_REFRESH_TOKEN:
                    return UnauthorizedResponse.JsonFromMessage("refresh token was already expired").collect();
            }
        }
        var response = new JsonObject();
        response.addProperty("status", "success");
        response.addProperty("token", newToken);
        return new CreatedResponse<>(response).collect();
    }

    /**
     * Logs a user using email and password combination. Responds with a short lived access-token and id of long lived refresh-token
     * <br><br>
     * <b>  API doc:</b><br>
     * <b>  Description</b>: Logs a user using email and password combination <br>
     * <b>  Method</b>: POST <br>
     * <b>  Location</b>: /auth/login <br>
     * <b>  Body</b>: <br>
     *     { <br>
     *     <i>
     *          email: [email],<br>
     *          password: [password],<br>
     *      </i>
     *      } <br>
     *
     * <b>  Success response</b>: {status: "success", data: {token, [token], refreshtoken: [refresh_token_id]}}, CODE: 201 <br>
     * <b>  Error response</b>: {status: "error", message: [error_message]}, 400 or 401
     *
     * @param credentials the credentials to log in with
     * @return an access token and refresh token id on success, error on fail.
     */
    @CrossOrigin(origins ="*", allowedHeaders="*")
    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> loginWithCredentials(@RequestBody(required = false) AuthCredentials credentials) {
        var genericError = ErrorResponse.JsonFromMessage("missing credentials");
        try { credentials.validate(); }
        catch (ValidationError | NullPointerException v)
            { return genericError.collect(); }

        if(authService.validateUserPassword(credentials.email(), credentials.password())) {
            var tokenPair = authService.generateAndSaveTokens(credentials.email());
            JsonObject data = new JsonObject();
            data.addProperty("token", tokenPair.first);
            data.addProperty("refreshtoken", tokenPair.second);
            return new CreatedResponse<>(data).collect();
        }
        return UnauthorizedResponse.JsonFromMessage("invalid login").collect();
    }

    /**
     * logs a specified user out, deletes the corresponding refresh token responds 200 whether the token exists or not
     * <br><br>
     * <b>  API doc:</b><br>
     * <b>  Description</b>: logs a specified user out, deletes the corresponding refresh token responds 200 whether the token exists or not<br>
     * <b>  Method</b>: POST <br>
     * <b>  Location</b>: /auth/logout <br>
     * <b>  Headers</b>:<br>
     * <b>  Refresh-Token</b>: the refresh token to use <br><br>
     * <b>  Success response</b>: {status: "ok", message: "no such token"|"token deleted"}, CODE: 200 <br>
     * <b>  Error response</b>: {status: "error", message: [error_message]}, 400 or 401
     *
     * @param refresh the refresh token id associated with the session
     * @return OK or Bad request
     */
    @CrossOrigin(origins ="*", allowedHeaders="*")
    @RequestMapping(value = "/auth/logout", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> logoutToken(@RequestHeader(value = "Refresh-Token", required = false) String refresh) {
        System.out.println("REFRESH-TOKEN ID:");
        System.out.println(refresh);
        if(refresh != null && authService.deleteRefreshToken(refresh))
            return OkResponse.JsonFromMessage("deleted").collect();
        return ErrorResponse.JsonFromMessage("no such token").collect();
    }

    public static class AuthCredentials implements ValidationError.Validatable {
        String password, email;

        public AuthCredentials(String email, String password) {
            this.email = email;
            this.password = password;
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
            throw new ValidationError(ERROR_CODE.NONE);
        }
    }
}

