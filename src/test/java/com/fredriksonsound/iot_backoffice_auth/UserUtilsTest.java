package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.util.UserUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserUtilsTest {

    @Test
    public void password_must_be_longer_than_5_characters() {
        assertThat(UserUtils.validPassword("Ab345")).isFalse();
        assertThat(UserUtils.validPassword("Ab3")).isFalse();
        assertThat(UserUtils.validPassword("123aBC")).isTrue();
    }

    @Test
    public void password_must_not_be_longer_than_32_characters() {
        assertThat(UserUtils.validPassword("123456789012345678901234567890AcE")).isFalse();
        assertThat(UserUtils.validPassword("123456789012345678901234567890Ac")).isTrue();
    }

    @Test
    public void password_must_contain_number() {
        assertThat(UserUtils.validPassword("abcde")).isFalse();
        assertThat(UserUtils.validPassword("AbCdE")).isFalse();
        assertThat(UserUtils.validPassword("Abc123")).isTrue();
    }

    @Test
    public void password_must_contain_uppercase() {
        assertThat(UserUtils.validPassword("abcde")).isFalse();
        assertThat(UserUtils.validPassword("123bcd")).isFalse();
        assertThat( UserUtils.validPassword("Abc123")).isTrue();
    }

    @Test
    public void password_must_contain_lowercase() {
        assertThat(UserUtils.validPassword("ABCDE")).isFalse();
        assertThat(UserUtils.validPassword("123BCD")).isFalse();
        assertThat(UserUtils. validPassword("Abc123")).isTrue();
    }

    @Test
    public void password_can_contain_special_characters_contain_lowercase() {
        assertThat(UserUtils.validPassword("@B1De")).isFalse();
        assertThat(UserUtils.validPassword("#123eD")).isTrue();
        assertThat(UserUtils. validPassword("@@@@@1eE")).isTrue();
    }

    @Test
    public void email_must_contain_exactly_1_ATSIGN_at_correct_position() {
        assertThat(UserUtils.validEmail("t@@a.se")).isFalse();
        assertThat(UserUtils.validEmail("@12.3BCD")).isFalse();
        assertThat(UserUtils.validEmail("Ab.c.123")).isFalse();
        assertThat(UserUtils.validEmail("Ab.c.123")).isFalse();
        assertThat(UserUtils.validEmail("test.example@com")).isFalse();
        assertThat(UserUtils.validEmail("test@example@.om")).isFalse();
        assertThat(UserUtils.validEmail("test@example.com")).isTrue();
    }

    @Test
    public void username_must_not_contain_special_characters() {
        assertThat(UserUtils.validUsername("aaa@")).isFalse();
        assertThat(UserUtils.validUsername("Magnus")).isTrue();
        assertThat(UserUtils.validUsername("Magnus1")).isTrue();
        assertThat(UserUtils.validUsername("Magnus#")).isFalse();
        assertThat(UserUtils.validUsername("!Magnus")).isFalse();
    }

    @Test
    public void username_length_must_be_longer_than_3_and_shorter_than_33() {
        assertThat(UserUtils.validUsername("aaa")).isFalse();
        assertThat(UserUtils.validUsername("12345678901234567890123456789012")).isTrue();
        assertThat(UserUtils.validUsername("123456789012345678901234567890123")).isFalse();
        assertThat(UserUtils.validUsername("Magnus")).isTrue();
    }
}
