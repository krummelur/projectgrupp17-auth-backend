package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.model.Agency;
import com.fredriksonsound.iot_backoffice_auth.model.RefreshToken;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DTOTests {
    /*No need to really test this class, except for toString, but for coverage*/
    @Test
    public void toString_handles_null() {
        User u = new User();
        assertThat(u.toString()).isEqualTo("User: {\n" +
                "\tusername:null,\n" +
                "\t email:null,\n" +
                "\t password_hash:null,\n" +
                "\t agency:null,\n" +
                "\t email:null\n" +
                "}");
    }

    @Test
    public void String_representation_is_correct() {
        User u = new User("Magnus", "test@example.com", "---", "agency_1");
        assertThat(u.toString()).isEqualTo("User: {\n" +
                "\tusername:Magnus,\n" +
                "\t email:test@example.com,\n" +
                "\t password_hash:---,\n" +
                "\t agency:agency_1,\n" +
                "\t email:test@example.com\n" +
                "}");
    }

    @Test
    public void pointless_test_for_coverage() {
        Agency a = new Agency();
        assertThat(a.name()).isEqualTo(null);
        assertThat(a.orgnr()).isEqualTo(null);

        RefreshToken t  = new RefreshToken();
        assertThat(t.id()).isEqualTo(null);
        assertThat(t.refresh_token()).isEqualTo(null);

        t  = new RefreshToken("id", "token");
        assertThat(t.id()).isEqualTo("id");
        assertThat(t.refresh_token()).isEqualTo("token");

        User u = new User("Magnus", "test@example.com", "---", "agency_1");
        assertThat(u.username()).isEqualTo("Magnus");
        assertThat(u.email()).isEqualTo("test@example.com");
        assertThat(u.pass_hash()).isEqualTo("---");
        assertThat(u.agency()).isEqualTo("agency_1");
    }
}