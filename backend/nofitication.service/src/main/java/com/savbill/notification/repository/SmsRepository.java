package com.savbill.notification.repository;


import java.util.List;
import java.util.Optional;

import com.savbill.notification.helper.SmsDataDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.notification.entity.Sms;
//@JaversSpringDataAuditable
@Repository
public interface SmsRepository extends JpaRepository<Sms, Long>,QuerydslPredicateExecutor<Sms>, JpaSpecificationExecutor<Sms>, QueryByExampleExecutor<Sms>
//JpaRepository<Sms, Long>,QuerydslPredicateExecutor<Sms> 
{
//	@Query("SELECT s FROM Sms s WHERE s.sourceName LIKE %:sourceName%")
//	List<Sms> findSmsBySourceName(@Param("sourceName") String sourceName);
	
	@Query("SELECT s FROM Sms s ORDER BY s.createDate DESC")
	List<Sms> findAll();
	
	Optional<Sms> findBySmsIdAndMvnoIdAndBuId(Long smsId, Long mvnoId,Long buId);

	List<Sms> findAllBySmsConfigIdAndMvnoIdAndBuIdAndStatusEqualsIgnoreCaseAndEventIdIsNotNull(Long smsconfigId , Long mvnoId , Long buId , String status);
	Page<Sms> findAllByServiceTypeContainingIgnoreCaseAndMvnoIdIn(String serviceType, List<Long> mvnoId,Pageable pageable);
	Page<Sms> findAllByServiceTypeContainingIgnoreCase(String serviceType, Pageable pageable);

	Page<Sms> findAll(Specification spec, Pageable pageable);


	@Query(value = "Select new com.savbill.notification.helper.SmsDataDTO(e.smsId, e.sourceName, e.countryCode, e.mobileNo, e.message, e.date, e.status, e.eventId, e.eventName) from Sms e where e.mvnoId =:mvnoId order by e.smsId desc ")
	Page<SmsDataDTO> findAllByMvnoId(@Param("mvnoId") Long mvnoId, Pageable pageable);
	@Query(value = "Select new com.savbill.notification.helper.SmsDataDTO(e.smsId, e.sourceName, e.countryCode, e.mobileNo, e.message, e.date, e.status, e.eventId, e.eventName) from Sms e where e.mvnoId =:mvnoId and e.buId IN (:buIdList) order by e.smsId desc")
	Page<SmsDataDTO> findAllByMvnoIdAndBuIdIn(@Param("mvnoId") Long mvnoId,@Param("buIdList") List<Long> buIdList, Pageable pageable);
}
