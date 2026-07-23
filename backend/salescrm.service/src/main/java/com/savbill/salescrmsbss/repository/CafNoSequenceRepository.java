package com.savbill.salescrmsbss.repository;

import javax.transaction.Transactional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CafNoSequence;

@JaversSpringDataAuditable
@Repository
public interface CafNoSequenceRepository extends JpaRepository<CafNoSequence, String>{

	@Modifying
	@Transactional
	@Query(value = "update CafNoSequence lms set lms.id =:id where lms.id=:Id")
	void updateSeq(@Param("id") String id,@Param("Id") String Id);
}
