package com.fredriksonsound.iot_backoffice_auth.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Token {
    @Id
    private String id;
    private String refresh_token;
    public String id() {return id;}
    public String refresh_token() { return refresh_token;}

    public Token() {}

    public Token(String id, String refresh_token) {
        this.id = id;
        this.refresh_token = refresh_token;
    }
}
