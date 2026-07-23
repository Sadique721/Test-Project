package com.savbill.inventorymanagement.modules.MasterManagement.ServiceAreaPincodeMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAreaPincodeRelRepository extends JpaRepository<ServiceAreaPincodeRel, Long>, QuerydslPredicateExecutor<ServiceAreaPincodeRel> {
    List<ServiceAreaPincodeRel> findAllByIsDeletedIsFalseAndServiceAreaIdIn(List<Long> serviceArea_id);

    List<ServiceAreaPincodeRel> findAllByIsDeletedIsFalseAndServiceAreaId(Long id);
}
