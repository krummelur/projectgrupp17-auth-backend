package com.fredriksonsound.iot_backoffice_auth.endpoint;

import com.fredriksonsound.iot_backoffice_auth.IotBackofficeAuthApplication;
import com.fredriksonsound.iot_backoffice_auth.web.OkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contains / mapping for checking if API is alive and what version.
 */
@RestController
public class Default {

    /**
     * Default endpoint
     * <br><br>
     * <b>API doc:</b><br>
     * Description: Returns the current API version as text <br>
     * method: GET <br>
     * Success response: "auth server v[API_VERSION]" <br>
     * Error response: N/A <br>
     * @return API version
     */
    @CrossOrigin(origins ="*", allowedHeaders="*")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> index() {
        return new OkResponse<>("auth server v"+ IotBackofficeAuthApplication.API_VERSION).collect();
    }
}
