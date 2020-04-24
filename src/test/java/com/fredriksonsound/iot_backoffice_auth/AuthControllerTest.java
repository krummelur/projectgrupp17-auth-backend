package com.fredriksonsound.iot_backoffice_auth;


import com.fredriksonsound.iot_backoffice_auth.service.AuthService;
import com.fredriksonsound.iot_backoffice_auth.endpoint.AuthController;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.service.ERROR_CODE;
import com.fredriksonsound.iot_backoffice_auth.service.Tokens;
import com.fredriksonsound.iot_backoffice_auth.util.Pair;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Tests the Auth endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthService authService;

    private String expiredAuthToken = "EXPIRED_AUTH_TKN";
    private String invalidAuthToken = "INVALID_AUTH_TKN";
    private String nonExpiredAuthToken = "NON_EXPIRED_AUTH_TKN";
    private String nonExpiredRefreshToken = "NON_EXPIRED_REFRESH_TKN";
    private String expiredRefreshToken = "EXPIRED_REFRESH_TKN";
    private String nonexistentRefreshToken = "NONEXISTENT_REFRESH_TKN";

    private AuthController.AuthCredentials successLogin =
            new AuthController.AuthCredentials("email@success.com", "password1");
    private AuthController.AuthCredentials invalidLogin =
            new AuthController.AuthCredentials("email@invalid.com", "password1");

    private String toJsonStr(AuthController.AuthCredentials a) {
        var obj = new JsonObject();
        obj.addProperty("email", a.email());
        obj.addProperty("password", a.password());
        return obj.toString();
    }

    @Before
    public void init() {
        this.mockMvc = standaloneSetup(new IotBackofficeAuthApplication()).build();
    }

    @Test
    public void login_user_ok() throws Exception {
        when(authService.generateAndSaveTokens(successLogin.email())).thenReturn(new Pair<>("t", "rt"));
        when(authService.validateUserPassword(successLogin.email(), successLogin.password())).thenReturn(true);

        this.mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(successLogin)))
                .andExpect(content().string(containsString("\"token\":\"t\"")))
                .andExpect(content().string(containsString("\"refreshtoken\":\"rt\"")))
                .andExpect(status().isCreated());
    }

    @Test
    public void login_user_invalid_login() throws Exception {
        when(authService.validateUserPassword(invalidLogin.email(), invalidLogin.password())).thenReturn(false);
        this.mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(invalidLogin)))
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"invalid login\"")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_user_missing_credentials() throws Exception {
        this.mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(new AuthController.AuthCredentials(null, null))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"missing credentials\"")));

        this.mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(new AuthController.AuthCredentials("email@test.com", null))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"missing credentials\"")));

        this.mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(new AuthController.AuthCredentials(null, "Password1"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"missing credentials\"")));
    }

    @Test
    public void refresh_nonexistent_refreshtoken() throws Exception {
        when(authService.refresh(expiredAuthToken, nonexistentRefreshToken)).thenThrow(new ValidationError(ERROR_CODE.NONEXISTENT_REFRESH_TOKEN));
        this.mockMvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", expiredAuthToken)
                .header("Refresh-Token", nonexistentRefreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"no such refresh token\"")));
    }

    @Test
    public void refresh_nonexpired_authtoken() throws Exception {
        when(authService.refresh(nonExpiredAuthToken, nonExpiredRefreshToken)).thenThrow(new ValidationError(ERROR_CODE.NONEXPIRED_ACCESS_TOKEN));
        this.mockMvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", nonExpiredAuthToken)
                .header("Refresh-Token", nonExpiredRefreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"auth token was not expired\"")));
    }

    @Test
    public void refresh_expired_refreshToken() throws Exception {
        when(authService.refresh(expiredAuthToken, expiredRefreshToken)).thenThrow(new ValidationError(ERROR_CODE.EXPIRED_REFRESH_TOKEN));
        this.mockMvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", expiredAuthToken)
                .header("Refresh-Token", expiredRefreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"refresh token was already expired\"")));
    }
    @Test
    public void invalid_accessToken() throws Exception {
        when(authService.refresh(invalidAuthToken, nonExpiredRefreshToken)).thenThrow(new ValidationError(ERROR_CODE.INVALID_JWT));
        this.mockMvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", invalidAuthToken)
                .header("Refresh-Token", nonExpiredRefreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"invalid jwt\"")));
    }

    @Test
    public void refresh_success() throws Exception {
        String tkn = Tokens.getAccessToken("UUID", "SOMEUSER");
        when(authService.refresh(expiredAuthToken, nonExpiredRefreshToken)).thenReturn(tkn);
        this.mockMvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .header("Auth-Token", expiredAuthToken)
                .header("Refresh-Token", nonExpiredRefreshToken))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"status\":\"success\"")))
                .andExpect(content().string(containsString("\"token\":\"" + tkn + "\"")));
    }
}
