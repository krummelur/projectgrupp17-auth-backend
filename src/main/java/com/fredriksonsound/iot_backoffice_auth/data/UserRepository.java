package com.fredriksonsound.iot_backoffice_auth.data;

import com.fredriksonsound.iot_backoffice_auth.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Database interaction interface.
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {
    boolean existsByUsername(String username);
}