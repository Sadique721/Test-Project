package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.FollowUpRemark;
import com.savbill.salescrmsbss.entity.LeadFollowUp;

@Repository
public interface FollowUpRemarkRepository extends JpaRepository<FollowUpRemark, Long>{

	List<FollowUpRemark> findByLeadFollowUp(LeadFollowUp leadFollowUp);
}
