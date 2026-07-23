package com.savbill.revenuemanagement.productmanagement.ServiceParameters.repository;

import com.savbill.revenuemanagement.productmanagement.ServiceParameters.domain.ServiceParameter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServcieParametersRepository extends JpaRepository<ServiceParameter,Long>{
    //ServiceParameter findAllByIdIn(Long Id);
}
