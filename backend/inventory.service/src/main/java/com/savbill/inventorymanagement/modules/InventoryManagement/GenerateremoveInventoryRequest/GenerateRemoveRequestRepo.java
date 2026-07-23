package com.savbill.inventorymanagement.modules.InventoryManagement.GenerateremoveInventoryRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GenerateRemoveRequestRepo extends JpaRepository<GenerateRemoveRequest,Long>, QuerydslPredicateExecutor<GenerateRemoveRequest> {
    GenerateRemoveRequest findByCustomerinventoryId(Long id);
    GenerateRemoveRequest findByCustomerinventoryIdAndCustomeridAndMacmappingid(Long customerInventoryId, Long customerId, Long macMappingId);
    GenerateRemoveRequest findByCustomerinventoryIdAndIsDeletedFalse(Long id);
    @Query("SELECT new GenerateRemoveRequest(grr.id, grr.revisedcharge, grr.requestStatus) FROM GenerateRemoveRequest grr WHERE grr.customerinventoryId = :customerInventoryId AND grr.isDeleted = false")
    GenerateRemoveRequest findRequestByCustomerInventoryId(@Param("customerInventoryId") Long customerInventoryId);

}
