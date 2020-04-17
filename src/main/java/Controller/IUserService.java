package Controller;

import com.fredriksonsound.iot_backoffice_auth.endpoint.RegisterController;
import com.fredriksonsound.iot_backoffice_auth.model.ValidationError;

/**
 * UserService interface.
 */
public interface IUserService {
    boolean saveNewUser(RegisterController.RegisterCredentials a) throws ValidationError;
}
