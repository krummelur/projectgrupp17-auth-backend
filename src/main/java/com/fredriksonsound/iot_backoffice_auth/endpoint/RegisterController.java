package com.fredriksonsound.iot_backoffice_auth.endpoint;

//import com.fredriksonsound.iot_backoffice_auth.Data.UserRepository;
//import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.Data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.web.ErrorResponse;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<JsonObject> registerUser(@RequestBody RegisterCredentials credentials) {
        try { credentials.validate(); } catch (ValidationError v) {
            return ErrorResponse.JsonFromMessage("Invalid json, missing key(s)").collect();
        }
        //User user = null;

        //try { user = userRepository.findById("test").get();}
        //catch (NoSuchElementException e) {e.printStackTrace();}

        //System.out.println(user);
        var gson = new JsonObject();
        gson.addProperty("Result", "OKAYYY");
        return new ResponseEntity<>(gson, new HttpHeaders(), HttpStatus.OK);
    }


    public class RegisterCredentials implements ValidationError.Validatable {
        private String username, password, email;
        public String username() {
            return username;
        }
        public String password() {
            return password;
        }
        public String email() {
            return email;
        }

        @Override
        public boolean validate() throws ValidationError {
            if (email != null && password != null && username != null)
                return true;
            throw new ValidationError("not a valid user");
        }
    }
}
