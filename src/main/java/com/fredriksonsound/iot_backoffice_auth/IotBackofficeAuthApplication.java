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
 */

package com.fredriksonsound.iot_backoffice_auth;

import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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
}