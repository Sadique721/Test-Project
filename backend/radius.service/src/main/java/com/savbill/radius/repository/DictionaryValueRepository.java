package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.DictionaryValue;

@Repository
public interface DictionaryValueRepository extends JpaRepository<DictionaryValue, Long>,QuerydslPredicateExecutor <DictionaryValue> {

	List<DictionaryValue> findByNameContaining(String name);
	
	Optional<DictionaryValue> findByName(String name);
	
	@Query(value = "select * from tbltdictionaryvalue d where d.dictionaryattributeid =:dictionaryAttributeId",nativeQuery = true)
	List<DictionaryValue> findByDictionaryAttributeId(@Param("dictionaryAttributeId") Long dictionaryAttributeId);
	
	@Query(value = "select * from tbltdictionaryvalue d where d.dictionaryvalueid !=:dictionaryvalueid AND d.name =:name",nativeQuery = true)
	Optional<DictionaryValue> findByNameOnUpdate(@Param("dictionaryvalueid") Long dictionaryvalueid, @Param("name") String name);
	
//	@Query(value = "select * from tbltdictionaryvalue d where d.name LIKE %:name% AND d.dictionaryattributeid =:dictionaryAttributeId",nativeQuery = true)
//	List<DictionaryValue> searchByName(@Param("name") String name,@Param("dictionaryAttributeId") Long dictionaryAttributeId);
	
//	@Query(value = "select * from tbltdictionaryvalue d where d.value =:value AND d.dictionaryattributeid =:dictionaryAttributeId",nativeQuery = true)
//	List<DictionaryValue> searchByValue(@Param("value") String value,@Param("dictionaryAttributeId") Long dictionaryAttributeId);
	
//	@Query(value = "select * from tbltdictionaryvalue d where d.name LIKE %:name% AND d.value =:value AND d.dictionaryattributeid =:dictionaryAttributeId",nativeQuery = true)
//	List<DictionaryValue> searchByNameAndValue(@Param("name") String name,@Param("value") String value,@Param("dictionaryAttributeId") Long dictionaryAttributeId);
}
