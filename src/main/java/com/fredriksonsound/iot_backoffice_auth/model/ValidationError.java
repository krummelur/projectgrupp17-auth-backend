package com.fredriksonsound.iot_backoffice_auth.model;

public class ValidationError extends Exception {
    public ValidationError(String mess) {
        super(mess);
    }
}
