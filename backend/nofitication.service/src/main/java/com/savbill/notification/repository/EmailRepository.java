package com.savbill.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.savbill.notification.helper.EmailDataDTO;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import com.savbill.notification.entity.Email;
//@JaversSpringDataAuditable
@Repository
@EnableJpaRepositories
public interface EmailRepository extends JpaRepository<Email, Long>,QuerydslPredicateExecutor<Email>, JpaSpecificationExecutor<Email>
{
	@Query("SELECT e FROM Email e ORDER BY createdOn DESC")
	List<Email> findAll();

	List<Email> findAll(Example ex);

	List<Email> findByMvnoId(Long mvnoId);
	List<Email> findByMvnoIdAndDateBetween(Long mvnoId, LocalDateTime startDate, LocalDateTime endDate);
	Optional<Email> findByEmailIdAndMvnoId(Long emailId, Long mvnoId);

	Optional<Email> findByEmailId(Long emailId);

	Page<Email> findAllByServiceTypeContainingIgnoreCaseAndMvnoIdIn(String serviceType, List<Long> mvnoId,Pageable pageable);
	Page<Email> findAllByServiceTypeContainingIgnoreCase(String serviceType, Pageable pageable);

	Page<Email> findAll(Specification<Email> spec, Pageable pageable);

//	@Query(value = "Select new com.savbill.notification.helper.EmailDataDTO(e.emailId, e.sourceName, e.emailAddress, e.message, e.date, e.status, e.event.eventId, e.event.eventName) from Email e where e.mvnoId =: mvnoid AND e.buId IN (:buIdList)")
//	Page<EmailDataDTO> findAllByMvnoIdAndBuIdIn(@Param("mvnoid") Long mvnoId, @Param("buIdList") List<Long> buIdList,Pageable pageable);


//	@Query(value = "SELECT e.emailId, e.sourceName, e.emailAddress, e.message, e.date, e.status, e.eventid, ev.eventName FROM tblmemail e INNER JOIN tblmevent ev ON e.eventid = ev.eventid WHERE e.mvnoid = :mvnoId",
//			nativeQuery = true)
	@Query(value = "Select new com.savbill.notification.helper.EmailDataDTO(e.emailId, e.sourceName, e.emailAddress, e.message, e.date, e.status, e.event.eventId, e.event.eventName) from Email e where e.mvnoId =:mvnoId  order by e.emailId desc ")
	Page<EmailDataDTO> findAllByMvnoId(@Param("mvnoId") Long mvnoId, Pageable pageable);
	@Query(value = "Select new com.savbill.notification.helper.EmailDataDTO(e.emailId, e.sourceName, e.emailAddress, e.message, e.date, e.status, e.event.eventId, e.event.eventName) from Email e where e.mvnoId =:mvnoId and e.buId IN (:buIdList) order by e.emailId desc ")
	Page<EmailDataDTO> findAllByMvnoIdAndBuIdIn(@Param("mvnoId") Long mvnoId,@Param("buIdList") List<Long> buIdList, Pageable pageable);


}
