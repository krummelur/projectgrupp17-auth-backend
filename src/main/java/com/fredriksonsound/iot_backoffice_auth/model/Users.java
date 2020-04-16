package com.fredriksonsound.iot_backoffice_auth.model;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Users {
    @Id private String username;
    private String email;
    private String pass_hash;
    private String agency;

    public Users() {};

    public Users(String username, String email, String pass_hash, String agency) {
        this.username = username;
        this.email = email;
        this.pass_hash = pass_hash;
        this.agency = agency;
    }




    @Override
    public String toString() {
        return new StringBuilder("User: {").
        append("{\n\tusername:").append(username).
        append(",\n\t email:").append(email).
        append(",\n\t password_hash:").
        append(",\n\t email=").append(email).
        append("\n}")
        .toString();
    }
}
