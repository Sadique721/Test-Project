package com.savbill.salescrmsbss.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CommonList;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface CommonListRepository extends JpaRepository<CommonList, Long>, QuerydslPredicateExecutor<CommonList> {
    List<CommonList> findAllByTypeAndStatusOrderByValueAsc(String type, String status);

    CommonList findByValue(String value);

    List<CommonList> findAllByTypeAndValue(String type, String value);

    List<CommonList> findAllByStatus(String status);

    List<CommonList> findAllByTypeAndStatusAndValue(String type, String status, String value);

    List<CommonList> findAllByTypeInAndStatus(List<String> type, String status);
}
