package com.savbill.salescrmsbss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.TemplateNotification;

@Repository
public interface NotificationTemplateRepository
		extends JpaRepository<TemplateNotification, Long>, QuerydslPredicateExecutor<TemplateNotification> {

	@Query(value = "Select * from TBLNOTIFICATIONEMPLATE  where templateName=:templateName", nativeQuery = true)
	Optional<TemplateNotification> findByTemplateName(@Param("templateName") String templateName);

	Integer countByEventEventId(Long eventId);

	@Query(value = "Select * from TBLNOTIFICATIONEMPLATE  where templateName like '%templateName%'", nativeQuery = true)
	List<TemplateNotification> findByName(@Param("templateName") String templateName);

	List<TemplateNotification> findAllByTemplateNameContainingIgnoreCase(String templateName);
}
