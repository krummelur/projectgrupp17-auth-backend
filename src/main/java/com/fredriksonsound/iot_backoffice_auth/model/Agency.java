package com.fredriksonsound.iot_backoffice_auth.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Database translation specification for agency table
 */
@Entity
public class Agency {
    @Id
    private String orgnr;
    private String name;
    public String name() { return name; }
    public String orgnr() { return orgnr; }

}
