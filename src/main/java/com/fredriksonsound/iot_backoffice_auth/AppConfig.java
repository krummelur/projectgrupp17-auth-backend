package com.fredriksonsound.iot_backoffice_auth;

import Controller.AuthService;
import Controller.UserService;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class AppConfig {
    @Bean
    public UserService userController() {
        return new UserService();
    }
    @Bean
    public AuthService authService() {return new AuthService();}
}
