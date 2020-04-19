package com.fredriksonsound.iot_backoffice_auth.endpoint;

import com.fredriksonsound.iot_backoffice_auth.IotBackofficeAuthApplication;
import com.fredriksonsound.iot_backoffice_auth.web.OkResponse;;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class Default {
    /**
     * Default endpoint
     * @return API version
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> index() {
        return new OkResponse<>("auth server v"+ IotBackofficeAuthApplication.API_VERSION).collect();
    }
}
