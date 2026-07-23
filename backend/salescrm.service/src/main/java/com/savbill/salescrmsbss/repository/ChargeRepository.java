package com.savbill.salescrmsbss.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Charge;
@JaversSpringDataAuditable
@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer>{

    Charge findByApiGatewayChargeId(Long id);
}
