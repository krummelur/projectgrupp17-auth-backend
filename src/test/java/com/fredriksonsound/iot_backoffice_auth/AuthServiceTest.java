package com.fredriksonsound.iot_backoffice_auth;

import Controller.*;
import com.fredriksonsound.iot_backoffice_auth.Data.TokenRepository;
import com.fredriksonsound.iot_backoffice_auth.Data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests the AuthService
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
public class AuthServiceTest {
    @TestConfiguration
    static class AuthServiceTestConf {
        @Bean
        public IAuthService userService() {
            return new AuthService();
        }
    }

    private String nonexistentUser = "test@nonexistent.com";
    private String existentUser = "test@existent.com";
    private String existentUserValidPassword = "Password1";
    private String existentUserInvalidPassword = "Password2";

    @MockBean
    TokenRepository tknRepository;

    @MockBean
    UserRepository uRepository;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void init() {
        User user = new User("magnus", "test@example.com", "-----", "123");
        when(uRepository.findById(nonexistentUser)).thenReturn(Optional.empty());
        when(uRepository.findById(existentUser)).thenReturn(Optional.of(new User("","",
                PasswordUtils.Hash(existentUserValidPassword)
                ,"")));
    }

    @Test
    public void validate_nonexistent_user_test() {
        assertThat(authService.validateUserPassword(nonexistentUser, "Password1")).isEqualTo(false);
    }

    @Test
    public void validate_valid_user_password() {
        assertThat(authService.validateUserPassword(existentUser, existentUserValidPassword)).isEqualTo(true);
    }

    @Test
    public void validate_invalid_user_password() {
        assertThat(authService.validateUserPassword(existentUser, existentUserInvalidPassword)).isEqualTo(false);
    }
}
