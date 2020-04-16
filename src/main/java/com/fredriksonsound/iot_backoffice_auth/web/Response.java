package com.fredriksonsound.iot_backoffice_auth.web;

import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;

public interface Response<T> {
    public ResponseEntity<T> collect();
}
