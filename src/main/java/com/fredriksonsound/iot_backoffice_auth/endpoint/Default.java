package com.fredriksonsound.iot_backoffice_auth.endpoint;

import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.web.CreatedResponse;
import com.fredriksonsound.iot_backoffice_auth.web.ErrorResponse;
import com.fredriksonsound.iot_backoffice_auth.web.OkResponse;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Default {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> index() {
        return new OkResponse<String>("auth server v0.0.1").collect();
    }
}
