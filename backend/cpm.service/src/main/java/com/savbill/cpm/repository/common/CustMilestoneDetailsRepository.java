package com.savbill.cpm.repository.common;

import com.savbill.cpm.model.common.CustMilestoneDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustMilestoneDetailsRepository extends JpaRepository<CustMilestoneDetails, Long> {

    List<CustMilestoneDetails> findAllByCustomers_id(Long customerId);
    List<CustMilestoneDetails> findAllByLeadMaster_id(Long leadId);
}
