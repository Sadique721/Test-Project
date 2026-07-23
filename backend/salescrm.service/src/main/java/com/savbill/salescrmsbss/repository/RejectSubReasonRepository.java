package com.savbill.salescrmsbss.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.RejectSubReason;

@JaversSpringDataAuditable
@Repository
public interface RejectSubReasonRepository extends JpaRepository<RejectSubReason, Long>{

}
