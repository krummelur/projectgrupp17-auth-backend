package com.fredriksonsound.iot_backoffice_auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Any spring config that needs to be done that cant be done in .properties
 */
@Configuration
@PropertySource("classpath:/application-${AUTH_ENVIRONMENT}.properties")
public class AppConfig {

}
