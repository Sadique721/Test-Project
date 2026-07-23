package com.savbill.notification.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.savbill.notification.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.notification.entity.Template;
//@JaversSpringDataAuditable
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long>, QuerydslPredicateExecutor<Template>
{

	@Query(value = "Select * from TBLMTEMPLATE where templateName=:templateName", nativeQuery = true)
	Optional<Template> findByTemplateName(@Param("templateName") String templateName);
	
	Integer countByEventEventId(Long eventId);


	List<Template> findAllByMvnoIdAndBuId(Integer mvnoId, Long buId);
	List<Template> findAllByMvnoId(Integer mvnoId);
	List<Template> findAllByMvnoIdAndBuIdIn(Integer mvnoId, List<Long> buId);
	List<Template>findAllByMvnoIdAndBuIdInAndEvent(Integer mvnoId, List<Long> buId,String event);
	List<Template>findAllByMvnoIdAndEvent(Integer mvnoId, String event);
	List<Template> findAllByMvnoIdAndBuId(Integer mvnoId, Integer buId);
//
@Query("SELECT t FROM Template t " +
		"WHERE t.mvnoId = :mvnoId " +
		"AND t.buId = :buId " +
		"AND t.event = :event")
Optional<Template> findTemplatesByMvnoBuAndEvent(@Param("mvnoId") Integer mvnoId, @Param("buId") Integer buId, @Param("event") Event event);

	Template findByTemplateId(Long id);
	Optional<Template> findByEvent_EventName(String eventName);
	Optional<Template> findByEvent_EventIdAndServiceTypeContainingIgnoreCase(Long event_eventId, String serviceType);
    List<Template> findByEvent_EventIdInAndServiceTypeContainingIgnoreCase(Set<Long> eventIds, String serviceTypeIwf);

	@Query(value = "Select * from TBLMTEMPLATE where templateName=:templateName AND mvnoId = :mvnoId ", nativeQuery = true)
	Optional<Template> findByTemplateNameAndMvnoId(@Param("templateName") String templateName ,@Param("mvnoId") Integer mvnoId );
}
