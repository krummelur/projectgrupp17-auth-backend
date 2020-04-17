package com.fredriksonsound.iot_backoffice_auth.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Database translation specification for refresh_token table
 */
@Entity
public class RefreshToken {
    @Id
    private String id;
    private String refresh_token;
    public String id() {return id;}
    public String refresh_token() { return refresh_token;}

    public RefreshToken() {}

    public RefreshToken(String id, String refresh_token) {
        this.id = id;
        this.refresh_token = refresh_token;
    }
}
