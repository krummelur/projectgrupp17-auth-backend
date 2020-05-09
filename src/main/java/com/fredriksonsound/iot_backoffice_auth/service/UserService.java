package com.fredriksonsound.iot_backoffice_auth.service;

import com.fredriksonsound.iot_backoffice_auth.data.AgencyRepository;
import com.fredriksonsound.iot_backoffice_auth.data.UserRepository;
import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;
import com.fredriksonsound.iot_backoffice_auth.model.User;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;
import com.fredriksonsound.iot_backoffice_auth.model.util.PasswordUtils;
import com.fredriksonsound.iot_backoffice_auth.model.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.el.ELContextEvent;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AgencyRepository agencyRepository;

    /**
     * Saves a new user to the database
     * @param credentials the user
     * @return true if success
     * @throws ValidationError
     */
    public boolean saveNewUser(RegisterController.RegisterCredentials credentials) throws ValidationError {
        ///TODO what about agency case sensitivity?
        var lcUsername = credentials.username().toLowerCase();
        var lcEmail  = credentials.email().toLowerCase();
        if (userRepository.existsById(lcEmail)
                || userRepository.existsByUsername(lcUsername))
            throw new ValidationError(ERROR_CODE.CONFLICTING_USER);

        if(!UserUtils.validEmail(credentials.email()))
            throw new ValidationError(ERROR_CODE.INVALID_EMAIL);
        if(!UserUtils.validUsername(credentials.username()))
            throw new ValidationError(ERROR_CODE.INVALID_USERNAME);
        if(!UserUtils.validPassword(credentials.password()))
            throw new ValidationError(ERROR_CODE.INVALID_PASSWORD);
        if(!agencyRepository.existsById(credentials.agency())) {
            throw new ValidationError(ERROR_CODE.NONEXISTENT_AGENCY);
        }

        User u = new User(lcUsername, lcEmail, PasswordUtils.Hash(credentials.password()), credentials.agency());
        userRepository.save(u);
        return true;
    }
}
