package com.fredriksonsound.iot_backoffice_auth;


import Controller.AuthService;
import Controller.UserService;
import com.fredriksonsound.iot_backoffice_auth.Data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.endpoint.AuthController;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.util.Pair;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AuthService authService;

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

    private boolean mockedLogin(RegisterController.RegisterCredentials u) {
        if(u.email().equals("email@invalid.com"))
            return false;
        if(u.email().equals("email@success.com"))
            return false;
        throw new RuntimeException();
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
}
