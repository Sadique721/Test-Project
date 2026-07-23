package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.DictionaryAttribute;

@Repository
public interface DictionaryAttributeRepository extends JpaRepository<DictionaryAttribute, Long> , QuerydslPredicateExecutor<DictionaryAttribute>
{

	List<DictionaryAttribute> findByNameContaining(String name);
	
	Optional<DictionaryAttribute> findByNameEqualsIgnoreCase(String name);

	@Query(value = "select * from tbltdictionaryattribute d where d.dictionaryid =:dictionaryId",nativeQuery = true)
	List<DictionaryAttribute> findByDictionaryId(@Param("dictionaryId") Long dictionaryId);
	
//	@Query(value = "select * from tbltdictionaryattribute d where d.dictionaryattributeid !=:dictionaryattributeid AND d.name =:name",nativeQuery = true)
//	Optional<DictionaryAttribute> findByNameOnUpdate(@Param("dictionaryattributeid") Long dictionaryattributeid, @Param("name") String name);
	
//	@Query(value = "select * from tbltdictionaryattribute d where d.name LIKE %:name% AND d.dictionaryid =:dictionaryId",nativeQuery = true)
//	List<DictionaryAttribute> searchDictionaryAttribute(@Param("name") String name,@Param("dictionaryId") Long dictionaryId);
}
