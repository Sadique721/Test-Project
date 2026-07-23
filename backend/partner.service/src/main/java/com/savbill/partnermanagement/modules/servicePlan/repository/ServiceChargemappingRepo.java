package com.savbill.partnermanagement.modules.servicePlan.repository;


import com.savbill.partnermanagement.modules.servicePlan.domain.ServiceChargeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargemappingRepo extends JpaRepository<ServiceChargeMapping, Long >
{

}
