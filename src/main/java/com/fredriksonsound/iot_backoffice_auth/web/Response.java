package com.fredriksonsound.iot_backoffice_auth.web;

import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;


/**
 * More ergonomic response interface
 * @param <T> body type
 */
public interface Response<T> {
    ResponseEntity<T> collect();
}
