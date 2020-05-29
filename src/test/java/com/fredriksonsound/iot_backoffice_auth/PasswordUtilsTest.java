package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.util.PasswordUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordUtilsTest {

    @Test
    public void hashed_passwords_can_be_verified() {
        var pWord = "myC0olP@ssword#!";
        var hash = PasswordUtils.Hash(pWord);
        assertThat(PasswordUtils.verify(pWord, hash)).isTrue();
        assertThat(PasswordUtils.verify(pWord+"!", hash)).isFalse();
        assertThat(PasswordUtils.verify("a", PasswordUtils.Hash("a"))).isTrue();
    }
}
