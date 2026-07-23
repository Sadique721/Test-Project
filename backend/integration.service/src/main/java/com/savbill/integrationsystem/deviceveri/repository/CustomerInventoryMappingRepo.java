package com.savbill.integrationsystem.deviceveri.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.CustomerInventoryMappingData;

@Repository
public interface CustomerInventoryMappingRepo extends JpaRepository<CustomerInventoryMappingData, Long>
{
	List<CustomerInventoryMappingData> findByItemIdAndIsDeleted(Long itemId, Integer isDeleted);
	
	List<CustomerInventoryMappingData> findByCustomerIdAndIsDeleted(Long customerId, Integer isDeleted);
	
	List<CustomerInventoryMappingData> findByCustomerIdAndItemIdNotNullAndIsDeleted(Long customerId, Integer isDeleted);
}
