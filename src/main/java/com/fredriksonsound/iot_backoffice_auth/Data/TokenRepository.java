package com.fredriksonsound.iot_backoffice_auth.Data;

import com.fredriksonsound.iot_backoffice_auth.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> { }