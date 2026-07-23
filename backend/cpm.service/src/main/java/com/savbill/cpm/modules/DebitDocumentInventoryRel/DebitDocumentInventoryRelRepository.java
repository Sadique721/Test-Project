package com.savbill.cpm.modules.DebitDocumentInventoryRel;

import com.savbill.cpm.model.postpaid.DebitDocumentInventoryRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface DebitDocumentInventoryRelRepository extends JpaRepository<DebitDocumentInventoryRel , Integer> , QuerydslPredicateExecutor<DebitDocumentInventoryRel> {
    DebitDocumentInventoryRel findByCustInventoryMappingId(Long custInventoryMappingId );


}
