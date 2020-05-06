package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.service.Tokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Optional;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
public class UsersControllerTest {
    private static String existentUserEmail = "EXISTENTEMAIL";
    private static String existentUserUsername = "EXISTENTUSER";
    private static String existentUserPassword = "EXISTENTPASSWORD";
    private static String existentUserAgency = "EXISTENTAGENCY";
    private static String nonExistentUserEmail = "NONEXISTENTEMAIL";
    private static String anotherExistantUserEMAIL = "ANOTHEREXISTENTEMAIL";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserRepository uRepository;

    @BeforeEach
    public void init() {
        when(uRepository.findById(existentUserEmail)).
                thenReturn(Optional.of(new User(existentUserUsername,existentUserEmail, existentUserPassword, existentUserAgency)));

        when(uRepository.findById(anotherExistantUserEMAIL)).
                thenReturn(Optional.of(new User(anotherExistantUserEMAIL,existentUserEmail, existentUserPassword, existentUserAgency)));

        when(uRepository.findById(nonExistentUserEmail)).thenReturn(Optional.empty());
    }

    @Test
    public void get_valid_user_test() throws Exception {
        String tknStr = Tokens.getCustomToken("TKNID", existentUserEmail, 999999);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Auth-Token", tknStr);
        this.mockMvc.perform(get("/users/" + existentUserEmail).header("Auth-Token", tknStr))
                .andExpect(content().string(containsString("\"status\":\"success\"")))
                .andExpect(content().string(containsString("\"email\":\"" + existentUserEmail+ "\"")))
                .andExpect(content().string(containsString("\"username\":\"" + existentUserUsername + "\"")))
                .andExpect(content().string(containsString("\"agency\":\"" + existentUserAgency + "\"")))
                .andExpect(status().isOk());
    }

    @Test
    public void get_user_without_accessToken() throws Exception {
        String tknStr = Tokens.getCustomToken("TKNID", nonExistentUserEmail, 999999);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Auth-Token", tknStr);
        this.mockMvc.perform(get("/users/" + nonExistentUserEmail))
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"missing Auth-Token header\"")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void get_nonexistant_user_test() throws Exception {
        String tknStr = Tokens.getCustomToken("TKNID", nonExistentUserEmail, 999999);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Auth-Token", tknStr);
        this.mockMvc.perform(get("/users/" + nonExistentUserEmail).header("Auth-Token", tknStr))
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"no user with such email found\"")))
                .andExpect(status().isNotFound());
    }
    @Test
    public void get_for_user_that_is_not_me() throws Exception {
        String tknStr = Tokens.getCustomToken("TKNID", anotherExistantUserEMAIL, 999999);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Auth-Token", tknStr);
        this.mockMvc.perform(get("/users/" + existentUserEmail).header("Auth-Token", tknStr))
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"access token does not grant access to that resource\"")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void get_user_with_expired_token() throws Exception {
        String tknStr = Tokens.getCustomToken("TKNID", existentUserEmail, -10000);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Auth-Token", tknStr);
        this.mockMvc.perform(get("/users/" + existentUserEmail).header("Auth-Token", tknStr))
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"expired access token\"")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void get_user_with_bad_token() throws Exception {
        String tknStr = Tokens.getCustomToken("TKNID", existentUserEmail, -10000);
        tknStr = tknStr.substring(1);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Auth-Token", tknStr);
        this.mockMvc.perform(get("/users/" + existentUserEmail).header("Auth-Token", tknStr))
                .andExpect(content().string(containsString("\"status\":\"error\"")))
                .andExpect(content().string(containsString("\"message\":\"JWT could not be parsed\"")))
                .andExpect(status().isBadRequest());
    }
}
