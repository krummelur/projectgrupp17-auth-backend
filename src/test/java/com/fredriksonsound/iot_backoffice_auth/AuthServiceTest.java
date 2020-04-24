package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.model.RefreshToken;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.service.*;
import com.fredriksonsound.iot_backoffice_auth.data.TokenRepository;
import com.fredriksonsound.iot_backoffice_auth.data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.model.util.PasswordUtils;
import com.fredriksonsound.iot_backoffice_auth.util.Pair;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Tests the AuthService
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.ANY)
public class AuthServiceTest {
    private String nonexistentUser = "test@nonexistent.com";
    private String existentUser = "test@existent.com";
    private String existentUserValidPassword = "Password1";
    private String existentUserInvalidPassword = "Password2";
    private String existentRefreshTkn = "TOKENID";
    private String nonExistentRefreshTkn = "NONEXISTENTREFRESH";
    private String expiredRefreshTkn = "EXPIREDREFRESHTOKEN";

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
        when(tknRepository.existsById(existentRefreshTkn)).thenReturn(true);
        when(tknRepository.existsById(expiredRefreshTkn)).thenReturn(true);
        String validRfTknData = Tokens.getRefreshToken(existentRefreshTkn, existentUser);
        String expiredRfTknData = Tokens.getCustomToken(expiredRefreshTkn, existentUser, -10000);
        when(tknRepository.findById(existentRefreshTkn)).thenReturn(Optional.of(new RefreshToken(existentRefreshTkn, validRfTknData)));
        when(tknRepository.findById(expiredRefreshTkn)).thenReturn(Optional.of(new RefreshToken(expiredRefreshTkn, expiredRfTknData)));
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

    @Test
    public void get_token() {
        //String uuid = UUID.randomUUID().toString();
        //String tkn = Tokens.getAccessToken(uuid, existentUser);
        Pair<String, String> tkns = authService.generateAndSaveTokens(existentUser);
        assertThat(((DefaultClaims)Tokens.decodeJwToken(tkns.first).getBody()).getSubject()).isEqualTo(existentUser);
    }

    @Test
    public void delete_token() {
        authService.deleteRefreshToken(existentRefreshTkn);
        authService.deleteRefreshToken(nonExistentRefreshTkn);
    }

    @Test
    public void non_existant_refresh_token_refresh() {
        var e = assertThrows(ValidationError.class, () -> authService.refresh("",nonExistentRefreshTkn));
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.NONEXISTENT_REFRESH_TOKEN);
    }

    @Test
    public void invalid_access_token_refresh() {
        String tokenString = Tokens.getAccessToken("SOMEID", "SOMEUSER");
        var b64Strings = tokenString.split("\\.");
        b64Strings[1] = new String(Base64.encodeBase64(new String(new Base64().decode(b64Strings[1]))
                .replace("SOMEUSER", "SOMEOTHERUSER").getBytes()));

        String encodedTamperedToken = b64Strings[0] + "." + b64Strings[1] + "." + b64Strings[2];

        var e = assertThrows(ValidationError.class, () -> authService.refresh(encodedTamperedToken,existentRefreshTkn));
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.INVALID_JWT);
    }

    @Test
    public void non_expired_access_token_refresh() {
        String tkn = authService.generateAndSaveTokens(existentUser).first;
        var e = assertThrows(ValidationError.class, () -> authService.refresh(tkn,existentRefreshTkn));
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.NONEXPIRED_ACCESS_TOKEN);
    }

    @Test
    public void expired_refresh_token() throws InterruptedException {
        String tkn = Tokens.getCustomToken("UUID", existentUser, -10000);
        Thread.sleep(10);

        var e = assertThrows(ValidationError.class, () -> authService.refresh(tkn,expiredRefreshTkn));
        assertThat(e.errorCode).isEqualTo(ERROR_CODE.EXPIRED_REFRESH_TOKEN);
    }

    @Test
    public void refresh_token_success() throws InterruptedException, ValidationError {
        String tkn = Tokens.getCustomToken("UUID", existentUser, -10000);
        Thread.sleep(10);

        assertThat(authService.refresh(tkn,existentRefreshTkn)).isNotEqualTo(null);
    }
}

