package com.savbill.partnermanagement.modules.MasterManagement.ServiceArea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Long>, QuerydslPredicateExecutor<ServiceArea> {
     List<ServiceArea> findAllByIdInAndStatusAndIsDeletedIsFalse(List<Long> result,String Status);
     List<ServiceArea> findAllByIdIn(List<Long> result);
}
