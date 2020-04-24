package com.fredriksonsound.iot_backoffice_auth.model;

import com.fredriksonsound.iot_backoffice_auth.service.ERROR_CODE;

/**
 * ValidationError instances represent an error in user submitted data
 * See <code>ERROR_CODE</code> enum.
 */
public class ValidationError extends Exception {
    public ERROR_CODE errorCode;
    public ValidationError(ERROR_CODE err) {
        super();
        this.errorCode = err;
    }

    public interface Validatable {
        boolean validate() throws ValidationError;
    }
}
