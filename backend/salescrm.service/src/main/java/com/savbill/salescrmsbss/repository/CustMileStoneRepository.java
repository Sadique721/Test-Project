package com.savbill.salescrmsbss.repository;

import com.savbill.salescrmsbss.entity.CustMilestoneDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustMileStoneRepository extends JpaRepository<CustMilestoneDetails, Long> {

    List<CustMilestoneDetails> findAllByLeadMaster_id(Long leadId);
}
