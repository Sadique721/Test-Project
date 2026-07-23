package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CustomerAddress;

@JaversSpringDataAuditable
@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {

	@Query(name = "select * from TBLMSUBSCRIBERADDRESSREL where lead_master_id=:leadId")
	List<CustomerAddress> findByLeadMasterId(@Param("leadId") Long leadId);

	CustomerAddress findByAddressTypeAndCustomerId(String addrType, Integer custId);
}
