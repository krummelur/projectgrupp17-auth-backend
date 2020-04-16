package Controller;

import com.fredriksonsound.iot_backoffice_auth.Data.AgencyRepository;
import com.fredriksonsound.iot_backoffice_auth.Data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.ERROR_CODE;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;
import com.fredriksonsound.iot_backoffice_auth.model.Users;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AgencyRepository agencyRepository;

    public void saveNewUser(RegisterController.RegisterCredentials credentials) throws ValidationError {
        //TODO: Check for both email and username, email should be primary.
        if (userRepository.existsById(credentials.username()))
            throw new ValidationError(ERROR_CODE.CONFLICTING_USER);

        if(!UserUtils.validPassword(credentials.password()))
            throw new ValidationError(ERROR_CODE.INVALID_PASSWORD);
        if(!UserUtils.validEmail(credentials.email()))
            throw new ValidationError(ERROR_CODE.INVALID_EMAIL);
        if(!UserUtils.validUsername(credentials.username()))
            throw new ValidationError(ERROR_CODE.INVALID_USERNAME);
        if(agencyRepository.findById(credentials.agency()).isEmpty()) {
            throw new ValidationError(ERROR_CODE.NONEXISTENT_AGENCY);
        }
        Users u = new Users(credentials.username(), credentials.email(), PasswordUtils.Hash(credentials.password()), credentials.agency());
        userRepository.save(u);
    }
}
