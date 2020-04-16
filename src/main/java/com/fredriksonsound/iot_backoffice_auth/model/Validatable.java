package com.fredriksonsound.iot_backoffice_auth.model;

public interface Validatable {
    boolean validate() throws ValidationError;
}
