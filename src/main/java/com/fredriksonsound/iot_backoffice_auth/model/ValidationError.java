package com.fredriksonsound.iot_backoffice_auth.model;

import com.fredriksonsound.iot_backoffice_auth.ERROR_CODE;

public class ValidationError extends Exception {
    public ERROR_CODE errorCode = ERROR_CODE.NONE;
    public ValidationError(ERROR_CODE err) {
        super();
        this.errorCode = err;
    }
    public ValidationError(String mess) {
        super(mess);
    }


    public interface Validatable {
        boolean validate() throws ValidationError;
    }
}
