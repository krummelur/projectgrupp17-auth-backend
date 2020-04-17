package com.fredriksonsound.iot_backoffice_auth;

/**
 * Error codes for the view layer to use in generating user messages
 */
public enum ERROR_CODE {
    NONE,
    INVALID_EMAIL,
    INVALID_PASSWORD,
    INVALID_USERNAME,
    CONFLICTING_USER,
    NONEXISTENT_AGENCY,
    NONEXISTENT_REFRESH_TOKEN,
    EXPIRED_REFRESH_TOKEN,
    NONEXPIRED_ACCESS_TOKEN,
    INVALID_JWT
}
