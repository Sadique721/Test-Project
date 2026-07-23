package com.savbill.radius.repository;

import com.savbill.radius.entity.ClearCacheMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClearCacheMappingRepository extends JpaRepository<ClearCacheMapping, Long>, QuerydslPredicateExecutor<ClearCacheMapping> {

    @Query("select c.id from ClearCacheMapping c where c.clientGroupId=:clientGroupId")
    List<Long> findAllByClientGroupId(@Param("clientGroupId") Long clientGroupId);
}
