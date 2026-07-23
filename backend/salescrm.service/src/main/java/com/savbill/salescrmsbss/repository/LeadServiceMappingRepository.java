package com.savbill.salescrmsbss.repository;

import com.savbill.salescrmsbss.entity.LeadServiceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadServiceMappingRepository extends JpaRepository<LeadServiceMapping, Long> {
   
	List<LeadServiceMapping> findByLeadId(Long leadId);

    List<LeadServiceMapping> findAllByServiceIdIn(List<Long> serviceIds);

    LeadServiceMapping findByConnectionNo(String connectionNo);
    
    List<LeadServiceMapping> findAllByServiceName(String serviceName);
}
