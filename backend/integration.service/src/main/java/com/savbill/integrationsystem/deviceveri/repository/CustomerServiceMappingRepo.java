package com.savbill.integrationsystem.deviceveri.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.CustomerServiceMappingData;

@Repository
public interface CustomerServiceMappingRepo extends JpaRepository<CustomerServiceMappingData, Long>
{
	List<CustomerServiceMappingData> findByConnectionNoAndIsDelete(String connectionNo, Integer isDelete);

	List<CustomerServiceMappingData> findByCustidAndIsDelete(Long custId, Integer isDelete);
}
