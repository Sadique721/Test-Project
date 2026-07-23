package com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface VendorRepo extends JpaRepository<Vendor,Long>, QuerydslPredicateExecutor<Vendor> {
    Page<Vendor> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
    Page<Vendor> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    Long countByNameAndIsDeletedIsFalse(String name);

    Long countByNameAndIsDeletedIsFalseAndMvnoIdIn(String name, List<Integer> mvnoId);

    Long countByNameAndIdAndIsDeletedIsFalse(String name, Long id);

    Long countByNameAndIdAndIsDeletedIsFalseAndMvnoIdIn(String name, Long id, List<Integer> mvnoId);

    Long countByIdAndIsDeletedIsFalse(Long id);

    List<Vendor> findAllByStatusAndIsDeletedIsFalse(String status);
    List<Vendor> findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(String status, List<Integer> mvnoId);
    Page<Vendor> findAllByIsDeletedIsFalse(Pageable pageable);
    Page<Vendor> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId, Pageable pageable);
}
