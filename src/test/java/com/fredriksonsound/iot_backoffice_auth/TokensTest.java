package com.fredriksonsound.iot_backoffice_auth;
import Controller.Tokens;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TokensTest {
    @Test
    public void testGetAccessToken() throws ValidationError {
        String tokenString = Tokens.getAccessToken("SOMEID", "SOMEUSER");
        DefaultClaims c = (DefaultClaims) Tokens.decodeJwToken(tokenString).getBody();
        assertThat("SOMEID").isEqualTo(c.getId());
        assertThat("SOMEUSER").isEqualTo(c.getSubject());
    }
}
