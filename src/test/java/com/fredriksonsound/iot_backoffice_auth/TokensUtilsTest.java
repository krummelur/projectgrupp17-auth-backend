package com.fredriksonsound.iot_backoffice_auth;
import com.fredriksonsound.iot_backoffice_auth.util.TokensUtils;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TokensUtilsTest {
    @Test
    public void encoded_token_should_be_decoded_correctly() throws ValidationError {
        final long expiryFromNow = System.currentTimeMillis() + TokensUtils.TOKEN_LIFETIME_MILLIS;
        String tokenString = TokensUtils.getAccessToken("SOMEID", "SOMEUSER");
        DefaultClaims c = (DefaultClaims) TokensUtils.decodeJwToken(tokenString).getBody();
        assertThat(c.getId()).isEqualTo("SOMEID");
        assertThat(c.getSubject()).isEqualTo("SOMEUSER");
        assertThat(c.getExpiration()).isCloseTo(new Date(expiryFromNow), 10000);
    }

    @Test
    public void access_expiry_is_correctly() throws ValidationError {
        final long expiryFromNow = System.currentTimeMillis() + TokensUtils.TOKEN_LIFETIME_MILLIS;
        String tokenString = TokensUtils.getAccessToken("SOMEID", "SOMEUSER");
        DefaultClaims c = (DefaultClaims) TokensUtils.decodeJwToken(tokenString).getBody();
        assertThat(c.getExpiration()).isCloseTo(new Date(expiryFromNow), 10000);
    }

    @Test
    public void refresh_expiry_is_correctly() throws ValidationError {
        String tokenString = TokensUtils.getRefreshToken("SOMEID", "SOMEUSER");
        DefaultClaims c = (DefaultClaims) TokensUtils.decodeJwToken(tokenString).getBody();
        var cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 30);
        var endDate = cal.toInstant().toEpochMilli();
        assertThat(c.getExpiration()).isCloseTo(new Date(endDate), 2000L);
    }

    @Test
    public void tampered_with_token_should_give_correct_error() throws ValidationError {
        String tokenString = TokensUtils.getAccessToken("SOMEID", "SOMEUSER");
        var b64Strings = tokenString.split("\\.");

        b64Strings[1] = new String(Base64.encodeBase64(new String(new Base64().decode(b64Strings[1]))
                .replace("SOMEUSER", "SOMEOTHERUSER").getBytes()));

        String encodedTamperedToken = b64Strings[0] + "." + b64Strings[1] + "." + b64Strings[2];
        assertThrows(SignatureException.class, () -> TokensUtils.decodeJwToken(encodedTamperedToken).getBody());
    }
}
