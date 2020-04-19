package com.fredriksonsound.iot_backoffice_auth.web;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * More ergonomic response
 * @param <T> response body type
 */
public class ErrorResponse<T> implements Response<T> {
    private final T body;
    private final HttpStatus status;

    public static ErrorResponse<JsonObject> JsonFromMessage(String message) {
        var json = new JsonObject();
        json.addProperty("status", "error");
        json.addProperty("message", message);
        return new ErrorResponse<>(json);
    }

    public ErrorResponse(T body, HttpStatus status) {
        this.body = body;
        this.status = status;
    }

    public ErrorResponse(T body) {
        this(body, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<T> collect() {
        return new ResponseEntity<>(this.body, this.status);
    }
}
