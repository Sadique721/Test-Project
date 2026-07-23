package com.savbill.partnermanagement.modules.ServiceParameters.repository;


import com.savbill.partnermanagement.modules.ServiceParameters.domain.ServiceParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServcieParametersRepository extends JpaRepository<ServiceParameter,Long>{
    //ServiceParameter findAllByIdIn(Long Id);
}
