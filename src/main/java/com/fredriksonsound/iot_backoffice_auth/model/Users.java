package com.fredriksonsound.iot_backoffice_auth.model;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Users {
    @Id private String email;
    private String username, pass_hash, agency;

    public Users() {};

    public Users(String username, String email, String pass_hash, String agency) {
        this.username = username;
        this.email = email;
        this.pass_hash = pass_hash;
        this.agency = agency;
    }

    public String email() {return email; }
    public String username() {return username; }
    public String pass_hash() {return pass_hash; }
    public String agency() {return agency; }

    @Override
    public String toString() {
        return new StringBuilder("User: {").
        append("{\n\tusername:").append(username).
        append(",\n\t email:").append(email).
        append(",\n\t password_hash:").append(pass_hash).
        append(",\n\t agency:").append(agency).
        append(",\n\t email=").append(email).
        append("\n}")
        .toString();
    }
}
