package com.savbill.salescrmsbss.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.savbill.salescrmsbss.entity.LeadFollowUp;

@JaversSpringDataAuditable
@Repository
public interface LeadFollowUpRepository extends JpaRepository<LeadFollowUp, Long>{

	@Query(name = "select * from TBLTLEADFOLLOWUP where follow_up_datetime>:fromTime and follow_up_datetime<=:toTime and is_missed = false and is_send = false and send_reminder_notification=false")
	Page<LeadFollowUp> findByFollowUpDatetimeBetween(@Param("fromTime") LocalDateTime fromTime,@Param("toTime") LocalDateTime toTime,Pageable pageable);
	
	@Query(name = "select * from TBLTLEADFOLLOWUP where is_no_followup_action=:noFollowUpAction")
	Page<LeadFollowUp> findByIsNoFollowupAction(@PathVariable("noFollowUpAction") boolean noFollowUpAction, Pageable pageable);
	
	@Query(name = "select * from TBLTLEADFOLLOWUP where lead_master_id=:leadMasterid")
	List<LeadFollowUp> findByLeadMasterId(@Param("leadMasterId") Long leadMasterId);
	
	LeadFollowUp findTopByOrderByIdDesc();

	@Query(name = "select * from TBLTLEADFOLLOWUP where assignee_id=:staffUserId AND status=:status",nativeQuery = true)
	Page<LeadFollowUp> findByStaffUserIdAndStatus(@Param("staffUserId") Integer staffUserId,@Param("status") String status,Pageable pageable);
	
	@Query(name = "select * from TBLTLEADFOLLOWUP where is_missed=:isMissed AND is_send=:isSend AND status=:status",nativeQuery = true)
	Page<LeadFollowUp> findByIsMissedAndIsSendAndStatus(@Param("isMissed") boolean isMissed,@Param("isSend") boolean isSend,@Param("status") String status,Pageable pageable);
	
}
