package com.savbill.radius.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuspendedProfileMappingRepository extends JpaRepository<SuspendedProfileMapping, Long>, QuerydslPredicateExecutor<SuspendedProfileMapping> {

    void deleteAllByClientGroupId(Long clientGroupId);

    List<SuspendedProfileMapping> findAllByClientGroupId(Long clientgrouid);
}
