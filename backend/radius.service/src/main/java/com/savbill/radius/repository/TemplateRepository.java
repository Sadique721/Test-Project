package com.savbill.radius.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Template;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long>, QuerydslPredicateExecutor<Template>
{

	@Query(value = "Select * from TBLMTEMPLATE where templateName=:templateName", nativeQuery = true)
	Optional<Template> findByTemplateName(@Param("templateName") String templateName);
	
	Integer countByEventEventId(Long eventId);

}