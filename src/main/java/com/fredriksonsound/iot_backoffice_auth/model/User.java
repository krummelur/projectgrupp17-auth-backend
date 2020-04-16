package com.fredriksonsound.iot_backoffice_auth.model;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id private String username;

    private String email;

    private String pass_hash;
    private String agency;

    public User() {

    }

    public User(String username, String email, String pass_hash, String agency) {
        this.username = username;
        this.email = email;
        this.pass_hash = pass_hash;
        this.agency = agency;
    }

    public void setnewUser(String name,String mail,String pass){
        setName(name);
        setEmail(mail);
        setpassword(pass);
        setAgency("111333");
    }

    private void setAgency(String agency){
        this.agency = agency;
    }

    public String getAgency(){
        return agency;
    }

    public String getPassword(){
        return pass_hash;
    }

    private void setpassword(String pass){
        this.pass_hash = pass;
    }


    public String getName() {
        return username;
    }

    private void setName(String name) {
        this.username = name;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
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
