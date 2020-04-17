package com.fredriksonsound.iot_backoffice_auth.web;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * More ergonomic response
 * @param <T> response body type
 */
public class UnauthorizedResponse<T> implements Response<T> {
    private final T body;
    private final HttpStatus status;

    public static UnauthorizedResponse<JsonObject> JsonFromMessage(String message) {
        var json = new JsonObject();
        json.addProperty("status", "error");
        json.addProperty("message", message);
        return new UnauthorizedResponse<>(json);
    }

    public UnauthorizedResponse(T body, HttpStatus status) {
        this.body = body;
        this.status = status;
    }

    public UnauthorizedResponse(T body) {
        this(body, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<T> collect() {
        return new ResponseEntity<>(this.body, this.status);
    }
}