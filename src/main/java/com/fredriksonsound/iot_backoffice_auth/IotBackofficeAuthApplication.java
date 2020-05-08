/**
 * Auth server for projektgrupp17
 * Supplies the following services:
 *
 * /auth/login 	=> logs in an existing user from email/password combination
 * /auth/logout => estroys the refresh token associated with access token
 * /register 	=> registers a new user
 * /refresh 	=> refreshes an expired auth token and returns the refreshed token
 * /			=> api version
 *
 * Author: Magnus Fredrikson
 * @version 1.0.1
 *
 * To run successfully you must set AUTH_ENVIRONMENT=TEST|LOCAL|STAGING|PRODUCTION
 * in gradle.build, local machine, or using -D
 * TEST is for testing, no database, only mock.
 * LOCAL is for local db
 * STAGING and PRODUCTION are for hosted db.
 */

package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties
@SpringBootApplication
public class IotBackofficeAuthApplication {
	public static final String API_VERSION = "1.0.1";

	/**
	 * Application entrypoint
	 * Starts a spring application
	 * @param args CliAgs
	 */
	public static void main(String[] args) {
		System.out.println("#############################\n!!!!  STARTING APP IN " + new Environment().ENVIRONMENT + "!!!\n#############################");
		SpringApplication.run(IotBackofficeAuthApplication.class, args);
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}
}