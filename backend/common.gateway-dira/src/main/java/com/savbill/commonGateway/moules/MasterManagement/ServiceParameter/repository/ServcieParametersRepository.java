package com.savbill.commonGateway.moules.MasterManagement.ServiceParameter.repository;


import com.savbill.commonGateway.moules.MasterManagement.ServiceParameter.domain.ServiceParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServcieParametersRepository extends JpaRepository<ServiceParameter,Long>, QuerydslPredicateExecutor<ServiceParameter> {
    ServiceParameter findAllById(Long Id);
}
