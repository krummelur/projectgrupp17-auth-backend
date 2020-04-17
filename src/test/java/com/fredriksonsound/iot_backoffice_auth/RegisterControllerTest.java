package com.fredriksonsound.iot_backoffice_auth;

import Controller.UserService;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController.RegisterCredentials;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Tests the Register endpoint
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RegisterControllerTest {

    private RegisterCredentials successUser =
            new RegisterCredentials("username", "Password1", "email@success.com", "agency");
    private RegisterCredentials invalidEmailIUser =
            new RegisterCredentials("username", "Password1", "email@invalidemail.com", "agency");
    private RegisterCredentials invalidUsernameUser =
            new RegisterCredentials("username", "Password1", "email@invalidusername.com", "agency");
    private RegisterCredentials conflictingUser =
            new RegisterCredentials("username", "Password1", "email@conflict.com", "agency");
    private RegisterCredentials nonexistantAgencyUser =
            new RegisterCredentials("username", "Password1", "email@nonexistentagency.com", "agency");

    private String toJsonStr(RegisterCredentials u) {
        var obj = new JsonObject();
        obj.addProperty("username", u.username());
        obj.addProperty("email", u.email());
        obj.addProperty("password", u.password());
        obj.addProperty("agency", u.agency());
        return obj.toString();
    }

    private boolean mockedSaveUser(RegisterCredentials u) throws ValidationError {
        if(u.email().equals("email@invalidemail.com"))
            throw new ValidationError(ERROR_CODE.INVALID_EMAIL);
        if(u.email().equals("email@invalidpassword.com"))
            throw new ValidationError(ERROR_CODE.INVALID_PASSWORD);
        if(u.email().equals("email@conflict.com"))
            throw new ValidationError(ERROR_CODE.CONFLICTING_USER);
        if(u.email().equals("email@nonexistentagency.com"))
            throw new ValidationError(ERROR_CODE.NONEXISTENT_AGENCY);
        if(u.email().equals("email@invalidusername.com"))
            throw new ValidationError(ERROR_CODE.INVALID_USERNAME);
        return true;
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService uService;

    @Before
    public void init() {
        this.mockMvc = standaloneSetup(new IotBackofficeAuthApplication()).build();
        User user = new User("magnus", "test@example.com", "-----", "123");
    }

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("auth server v")));
    }

    @Test
    public void when_register_user_correct() throws Exception {
        when(uService.saveNewUser(Mockito.any(RegisterCredentials.class))).thenAnswer((invocation) -> {
            var u = (RegisterCredentials) invocation.getArgument(0);
            return mockedSaveUser(u);
        });

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(successUser))).andExpect(status().isCreated());
    }

    @Test
    public void it_gives_correct_error_when_invalid_email() throws Exception {
        when(uService.saveNewUser(Mockito.any(RegisterCredentials.class))).thenAnswer((invocation) -> {
            var u = (RegisterCredentials) invocation.getArgument(0);
            return mockedSaveUser(u);
        });
        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(invalidEmailIUser))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("invalid email")));
    }

    @Test
    public void it_gives_correct_error_when_invalid_username() throws Exception {
        when(uService.saveNewUser(Mockito.any(RegisterCredentials.class))).thenAnswer((invocation) -> {
            var u = (RegisterCredentials) invocation.getArgument(0);
            return mockedSaveUser(u);
        });
        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(invalidUsernameUser))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("invalid username")));
    }

    @Test
    public void it_gives_correct_error_when_conflicting_iuser() throws Exception {
        when(uService.saveNewUser(Mockito.any(RegisterCredentials.class))).thenAnswer((invocation) -> {
            var u = (RegisterCredentials) invocation.getArgument(0);
            return mockedSaveUser(u);
        });
        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(conflictingUser))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("user or email aready taken")));
    }

    @Test
    public void it_gives_correct_error_when_nonexistent_agency() throws Exception {
        when(uService.saveNewUser(Mockito.any(RegisterCredentials.class))).thenAnswer((invocation) -> {
            var u = (RegisterCredentials) invocation.getArgument(0);
            return mockedSaveUser(u);
        });
        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(nonexistantAgencyUser))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("agency does not exist")));
    }

    @Test
    public void it_gives_correct_error_when_missing_keys() throws Exception {
        when(uService.saveNewUser(Mockito.any(RegisterCredentials.class))).thenAnswer((invocation) -> {
            var u = (RegisterCredentials) invocation.getArgument(0);
            return mockedSaveUser(u);
        });
        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(new RegisterCredentials(null, "pass", "email", "agency")))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid body, missing key(s)")));

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(new RegisterCredentials("username", null, "email", "agency")))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid body, missing key(s)")));

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(new RegisterCredentials("username", "pass", null, "agency")))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid body, missing key(s)")));

        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(new RegisterCredentials("username", "pass", "email", null)))).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid body, missing key(s)")));
    }

    @Test
    public void it_has_correct_status_when_wrong_content_type() throws Exception {
        when(uService.saveNewUser(Mockito.any(RegisterCredentials.class))).thenAnswer((invocation) -> {
            var u = (RegisterCredentials) invocation.getArgument(0);
            return mockedSaveUser(u);
        });
        this.mockMvc.perform(post("/users").contentType(MediaType.TEXT_PLAIN)
                .content(toJsonStr(nonexistantAgencyUser))).andExpect(status().isUnsupportedMediaType());
    }
}
