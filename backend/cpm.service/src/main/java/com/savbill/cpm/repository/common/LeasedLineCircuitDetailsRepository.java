package com.savbill.cpm.repository.common;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.common.LeasedLineCircuitDetails;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface LeasedLineCircuitDetailsRepository extends JpaRepository<LeasedLineCircuitDetails, Integer>{

	//LeasedLineCircuitDetails findByLlcIdentifier(String llcIdentifier);

	List<LeasedLineCircuitDetails> findByLlcIdentifier(String llcIdentifier);
}
