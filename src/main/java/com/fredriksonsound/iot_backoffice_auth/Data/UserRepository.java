package com.fredriksonsound.iot_backoffice_auth.Data;

import com.fredriksonsound.iot_backoffice_auth.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User, String> { }