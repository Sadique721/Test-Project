package com.savbill.radius.repository;

import com.savbill.radius.entity.DynamicAttributeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DynamicAttributeMappingRepository  extends JpaRepository<DynamicAttributeMapping, Long>, QuerydslPredicateExecutor<DynamicAttributeMapping> {

    void deleteAllByClientGroupId(Long clientGroupId);

    List<DynamicAttributeMapping> findAllByClientGroupId(Long clientGroupId);
}
