package com.fredriksonsound.iot_backoffice_auth;

import org.springframework.http.ResponseEntity;

public interface Response<T> {
    public ResponseEntity<T> collect();
}
