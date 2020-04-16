package com.fredriksonsound.iot_backoffice_auth.web;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CreatedResponse<T> implements Response<T> {
    final T body;

    public static CreatedResponse<JsonObject> JsonFromMessage(String message) {
        var json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("message", message);
        return new CreatedResponse<>(json);
    }

    public CreatedResponse(T body) {
        this.body = body;
    }

    @Override
    public ResponseEntity<T> collect() {
        return new ResponseEntity<T>(this.body, HttpStatus.CREATED);
    }
}
