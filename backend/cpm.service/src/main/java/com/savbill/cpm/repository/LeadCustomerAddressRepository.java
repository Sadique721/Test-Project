package com.savbill.cpm.repository;

import com.savbill.cpm.model.lead.LeadCustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface LeadCustomerAddressRepository extends JpaRepository<LeadCustomerAddress, Integer> {

    @Query(name = "select * from TBLMLEADSUBSCRIBERADDRESSREL where lead_master_id=:leadId")
    List<LeadCustomerAddress> findByLeadMasterId(@Param("leadId") Long leadId);

    @Query(name = "select * from TBLMLEADSUBSCRIBERADDRESSREL where lead_master_id=:leadId and address_type=:addressType")
    List<LeadCustomerAddress> findByAddressTypeAndLeadMasterId(@Param("addressType") String addressType, @Param("leadId") Long leadId);
}
