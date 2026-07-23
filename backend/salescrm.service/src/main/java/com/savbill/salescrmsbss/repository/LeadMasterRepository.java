package com.savbill.salescrmsbss.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.LeadMaster;

@JaversSpringDataAuditable
@Repository
public interface LeadMasterRepository extends JpaRepository<LeadMaster, Long>, QuerydslPredicateExecutor<LeadMaster> {

	@Query(value = "select * from tblmleadmaster where isDeleted=false and mobile=:mobileno",nativeQuery = true)
	List<LeadMaster> findByMobile(@Param("mobileno") String mobileno);
	
//	@Query(value = "select * from tblmleadmaster where isDeleted=false and mvnoId=:mvnoId and buId=:buId and mobile=:mobileno",nativeQuery = true)
//	List<LeadMaster> findByMobileAndMvnoIdAndBuId(@Param("mobileno") String mobileno,@Param("mvnoId") Long mvnoId,@Param("buId") List<Long> buId);

	@Query(value = "select * from tblmleadmaster where isDeleted=false and mvnoId=:mvnoId and buId IN (:buId) and mobile=:mobileno", nativeQuery = true)
	List<LeadMaster> findByMobileAndMvnoIdAndBuId(@Param("mobileno") String mobileno, @Param("mvnoId") Long mvnoId, @Param("buId") List<Long> buId);


	@Query(value = "select * from tblmleadmaster where isDeleted=false and mvnoId=:mvnoId and mobile=:mobileno",nativeQuery = true)
	List<LeadMaster> findByMobileAndMvnoId(@Param("mobileno") String mobileno,@Param("mvnoId") Long mvnoId);

//	@Query(value = "select * from tblmleadmaster where isDeleted=false AND (lower(firstname) like '%' || :search || '%' OR  lower(lastname) like '%' || :search || '%' OR lower(mobile) like '%' || :search || '%' OR created_by=:search", 
//			countQuery = "select count(*) from tblmleadmaster where isDeleted=false AND AND (lower(firstname) like '%' || :search || '%' OR  lower(lastname) like '%' || :search || '%' OR lower(mobile) like '%' || :search || '%' OR created_by=:search", nativeQuery = true)
//	Page<LeadMaster> searchEntity(@Param("search") String search, Pageable pageable);
	
//	@Query(value = "select * from tblmleadmaster where isDeleted=false AND (lower(firstname) like '%' || :search || '%' OR  lower(lastname) like '%' || :search || '%')",nativeQuery = true) 
//	Page<LeadMaster> searchEntity(@Param("search") String search,Pageable pageable);
	
	@Query(value = "select lm from LeadMaster lm where lm.isDeleted=false AND (lower(lm.firstname) like '%'||:name||'%' OR lower(lm.lastname) like '%'||:name||'%')") 
	Page<LeadMaster> searchEntity(@Param("name") String name,Pageable pageable);

	@Query(value = "select lm from LeadMaster lm where lm.isDeleted=false AND mvnoId=:mvnoId AND (lower(lm.firstname) like '%'||:name||'%' OR lower(lm.lastname) like '%'||:name||'%')")
	Page<LeadMaster> searchEntity(@Param("name") String name,@Param("mvnoId") Long mvnoId,Pageable pageable);
		
	Page<LeadMaster> findByMobileContainingAndIsDeleted(String mobile,Boolean isDeleted,Pageable pageable);

	Page<LeadMaster> findByMobileContainingAndMvnoIdAndIsDeleted(String mobile,Long mvnoId,Boolean isDeleted,Pageable pageable);

	Page<LeadMaster> findByLeadStatusAndMvnoIdAndIsDeleted(String status,Long mvnoId,Boolean isDeleted,Pageable pageable);
	Page<LeadMaster> findByLeadStatusAndIsDeleted(String status,Boolean isDeleted,Pageable pageable);


	@Query(value = "select lm from LeadMaster lm where isDeleted=false AND lastModifiedOn BETWEEN :fromDate AND :toDate")
	Page<LeadMaster> searchLastModifiedOn(@Param("fromDate") LocalDateTime fromDate,@Param("toDate") LocalDateTime toDate,Pageable pageable);

	@Query(value = "select lm from LeadMaster lm where isDeleted=false AND lastModifiedOn BETWEEN :fromDate AND :toDate AND mvnoId=:mvnoId")
	Page<LeadMaster> searchLastModifiedOnAndMvnoId(@Param("fromDate") LocalDateTime fromDate,@Param("toDate") LocalDateTime toDate,@Param("mvnoId") Long mvnoId,Pageable pageable);

	Page<LeadMaster> findByLastModifiedOnAndIsDeleted(LocalDateTime date,Boolean isDeleted,Pageable pageable);

	Page<LeadMaster> findByCreatedByAndMvnoIdAndIsDeleted(String createdBy,Long mvnoId,Boolean isDeleted,Pageable pageable);
	Page<LeadMaster> findByCreatedByAndIsDeleted(String createdBy,Boolean isDeleted,Pageable pageable);

	@Query(name = "select * from tblmleadmaster where lead_sub_source_id=:leadSubSourceId",nativeQuery = true)
	List<LeadMaster> findByLeadSubSourceId(@Param("leadSubSourceId") Long leadSubSourceId);

	@Query(name = "select * from tblmleadmaster where lead_source_id=:leadSourceId",nativeQuery = true)
	List<LeadMaster> findByLeadSourceId(@Param("leadSourceId") Long leadSourceId);

	@Query(value = "select count(*) from LeadMaster t where t.staff_id =:s1 and t.lead_master_id = :s2", nativeQuery = true)
	Long findMinimumApprovalReuqestByStaff(@Param("s1") Integer id,@Param("s2") Integer custid);

	@Query(name = "select * from tblmleadmaster where reject_reason_id=:rejectReasonId",nativeQuery = true)
	List<LeadMaster> findByRejectReasonId(@Param("rejectReasonId") Long rejectReasonId);

	@Query(name = "select * from tblmleadmaster where reject_sub_reason_id=:rejectSubReasonId",nativeQuery = true)
	List<LeadMaster> findByRejectSubReasonId(@Param("rejectSubReasonId") Long rejectSubReasonId);
	
	@Query(name = "select * from tblmleadmaster where isDeleted=false AND no_lead_followup_send_notification=:noLeadFollowupSendNotification",nativeQuery = true)
	Page<LeadMaster> findByNoLeadFollowupSendNotification(@Param("noLeadFollowupSendNotification") boolean noLeadFollowupSendNotification,Pageable pageable);
	
	@Query(name = "select * from tblmleadmaster where isDeleted=false AND lead_status='Inquiry' AND next_approve_staff_id=:nextApproveStaffId",nativeQuery = true)
	Page<LeadMaster> findByNextApproveStaffId(@Param("nextApproveStaffId") Integer nextApproveStaffId,Pageable pageable);

	List<LeadMaster> findAllByUsernameAndMvnoId(String username,Long mvnoId );

	List<LeadMaster> findAllByUsernameAndMvnoIdAndBuId(String username,Long mvnoId,Long buId );


	List<LeadMaster> findAllByBuIdAndIsDeleted(Long buid, Boolean isDeleted);


    @Query(value = "SELECT COUNT(*) FROM tblmleadmaster WHERE lead_status IN ('Inquiry','Re-Inquiry') AND  next_approve_staff_id=:nextApproveStaffId AND isDeleted = false",nativeQuery = true)
    int countNewActivation(@Param("nextApproveStaffId") Integer nextApproveStaffId);
}
