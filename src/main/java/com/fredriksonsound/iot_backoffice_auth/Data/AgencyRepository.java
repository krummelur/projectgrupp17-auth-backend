package com.fredriksonsound.iot_backoffice_auth.Data;

import com.fredriksonsound.iot_backoffice_auth.model.Agency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyRepository extends CrudRepository<Agency, String> { }
