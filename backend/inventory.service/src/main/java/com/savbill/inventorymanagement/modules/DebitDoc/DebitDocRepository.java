package com.savbill.inventorymanagement.modules.DebitDoc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface DebitDocRepository extends JpaRepository<DebitDocument, Integer>, QuerydslPredicateExecutor<DebitDocument> {
    DebitDocument findByInventoryMappingId(Long id);
}
