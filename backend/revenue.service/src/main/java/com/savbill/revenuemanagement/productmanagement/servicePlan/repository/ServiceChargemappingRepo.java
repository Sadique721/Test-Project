package com.savbill.revenuemanagement.productmanagement.servicePlan.repository;


import com.savbill.revenuemanagement.productmanagement.servicePlan.domain.ServiceChargeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargemappingRepo extends JpaRepository<ServiceChargeMapping, Long >
{

}
