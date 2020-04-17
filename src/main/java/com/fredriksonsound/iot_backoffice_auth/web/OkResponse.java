package com.fredriksonsound.iot_backoffice_auth.web;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * More ergonomic response
 * @param <T> response body type
 */
public class OkResponse<T> implements Response<T> {
    final T body;

    public static OkResponse<JsonObject> JsonFromMessage(String message) {
        var json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("message", message);
        return new OkResponse<JsonObject>(json);
    }

    public OkResponse(T body) {
        this.body = body;
    }

    @Override
    public ResponseEntity<T> collect() {
        return new ResponseEntity<T>(this.body, HttpStatus.OK);
    }
}
