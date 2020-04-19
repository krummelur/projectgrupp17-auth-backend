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
 * @version 1.0.0
 */

package com.fredriksonsound.iot_backoffice_auth;

import Controller.Tokens;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@SpringBootApplication
public class IotBackofficeAuthApplication {
	/**
	 * Application entrypoint
	 * Starts a spring application
	 * @param args CliAgs
	 */
	public static void main(String[] args) throws ValidationError {
		System.out.println("#############################\n!!!!  STARTING APP IN " + new Environment().ENVIRONMENT + "!!!\n#############################");

		SpringApplication.run(IotBackofficeAuthApplication.class, args);
	}
}