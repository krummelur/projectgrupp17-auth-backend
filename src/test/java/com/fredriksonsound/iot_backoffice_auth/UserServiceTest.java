package com.fredriksonsound.iot_backoffice_auth;

import Controller.IUserService;
import Controller.UserService;
import com.fredriksonsound.iot_backoffice_auth.Data.AgencyRepository;
import com.fredriksonsound.iot_backoffice_auth.Data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests the UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
public class UserServiceTest {
    @TestConfiguration
    static class UserServiceTestConf {
        @Bean
        public IUserService userService() {
            return new UserService();
        }
    }

    @MockBean
    AgencyRepository agencyRepository;
    @MockBean
    UserRepository uRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void init() {
        User user = new User("magnus", "test@example.com", "-----", "123");
        when(uRepository.findById("test@example.com")).thenReturn(Optional.of(user));
        when(uRepository.existsById("test@example.com")).thenReturn(true);
        when(agencyRepository.existsById("123")).thenReturn(true);
    }

    @Test
    public void it_should_register_successfully() throws ValidationError {
        RegisterController.RegisterCredentials rc =
                new RegisterController.RegisterCredentials("unique", "abC123", "unique@example.com", "123");
        userService.saveNewUser(rc);
    }

    @Test
    public void it_throws_correct_error_on_on_conflicting_user() {
        RegisterController.RegisterCredentials rc =
                new RegisterController.RegisterCredentials("magnus", "abC123", "test@example.com", "123");

        ValidationError e = assertThrows(ValidationError.class, () -> { userService.saveNewUser(rc); }, "validationException thrown when adding existing user" );
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.CONFLICTING_USER);
    }

    @Test
    public void it_throws_correct_error_on_on_invalid_email() {
        RegisterController.RegisterCredentials rc =
                new RegisterController.RegisterCredentials("unique", "abC123", "test@e@xample.com", "123");
        ValidationError e = assertThrows(ValidationError.class, () -> { userService.saveNewUser(rc); }, "validationException thrown when adding existing user" );
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.INVALID_EMAIL);

        RegisterController.RegisterCredentials rc2 = new RegisterController.RegisterCredentials("unique", "abC123", "test@example", "123");
        e = assertThrows(ValidationError.class, () -> { userService.saveNewUser(rc2); }, "validationException thrown when adding existing user" );
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.INVALID_EMAIL);

        RegisterController.RegisterCredentials rc3 = new RegisterController.RegisterCredentials("unique", "abC123", "test_example.com", "123");
        e = assertThrows(ValidationError.class, () -> { userService.saveNewUser(rc3); }, "validationException thrown when adding existing user" );
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.INVALID_EMAIL);
    }

    @Test
    public void it_throws_correct_error_on_insecure_password() {
        RegisterController.RegisterCredentials rc =
                new RegisterController.RegisterCredentials("unique", "abc123", "unique@example.com", "123");

        ValidationError e = assertThrows(ValidationError.class, () -> { userService.saveNewUser(rc); }, "validationException thrown when adding existing user" );
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.INVALID_PASSWORD);
    }

    @Test
    public void it_throws_correct_error_on_nonexistent_agency() {
        RegisterController.RegisterCredentials rc =
                new RegisterController.RegisterCredentials("unique", "abC123", "unique@example.com", "1234");

        ValidationError e = assertThrows(ValidationError.class, () -> { userService.saveNewUser(rc); }, "validationException thrown when adding existing user" );
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.NONEXISTENT_AGENCY);
    }
}