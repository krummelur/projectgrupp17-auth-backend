package com.fredriksonsound.iot_backoffice_auth;

import Controller.UserService;
import com.fredriksonsound.iot_backoffice_auth.Data.AgencyRepository;
import com.fredriksonsound.iot_backoffice_auth.Data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController.RegisterCredentials;

import java.util.Optional;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;



@SpringBootTest
@AutoConfigureMockMvc
public class RegisterControllerTest {
    //Mock users give specific responses from userService
    private RegisterCredentials successUser =
            new RegisterCredentials("username", "Password1", "email@success.com", "agency");
    private RegisterCredentials invalidEmailIUser =
            new RegisterCredentials("username", "Password1", "email@invalidemail.com", "agency");
    private RegisterCredentials invalidUsernameUser =
            new RegisterCredentials("username", "Password1", "email@invalidUsername.com", "agency");
    private RegisterCredentials conflictingUser =
            new RegisterCredentials("username", "Password1", "email@conflict.com", "agency");
    private RegisterCredentials nonexistantAgencyUser =
            new RegisterCredentials("username", "Password1", "email@nonexitantagency.com", "agency");

    private String toJsonStr(RegisterCredentials u) {
        var obj = new JsonObject();
        obj.addProperty("username", u.username());
        obj.addProperty("email", u.email());
        obj.addProperty("password", u.password());
        obj.addProperty("agency", u.agency());
        return obj.toString();
    }

    //@LocalServerPort
    //private String port;

    //@Autowired
    //private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AgencyRepository agencyRepository;
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
        //doNothing().doThrow(new IllegalArgumentException()).when(uService).saveNewUser(successUser);
        doNothing().when(uService).saveNewUser(successUser);
        //doNothing().when(uService.saveNewUser(successUser)).doNo
        //doNothing().when(uService.saveNewUser(successUser)));
        this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(toJsonStr(successUser))).andExpect(status().isCreated());
    }
}
