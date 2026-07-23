package com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface ExternalItemManagementRepository extends JpaRepository<ExternalItemManagement, Long>, QuerydslPredicateExecutor<ExternalItemManagement> {
    @Query(nativeQuery = true, value = "select * from tblmexternalitemmanagement t where t.product_id =:id")
    List<ExternalItemManagement> findAllByProductId(@Param("id") Integer id);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblhitemhistory tiowmm where tiowmm.external_item_id =:id and tiowmm.is_deleted =false\n" +
            "union all\n" +
            "select count(*) as tab from tbltcustomerinventorymapping t2 where t2.external_item_id =:id and t2.is_deleted =false\n" +
            ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);
    Page<ExternalItemManagement> findAllByIdIn(List<Long> ids, Pageable pageable);

    List<ExternalItemManagement> findAllByServiceAreaIdIdInAndIsDeletedIsFalseAndMvnoIdIn(List<Long> serviceAreaId_id, List<Integer> mvnoId);
    Page<ExternalItemManagement> findAllByexternalItemGroupNumberContainingIgnoreCaseAndIsDeletedIsFalse(String externalItemGroupNumber, Pageable pageable);
    List<ExternalItemManagement> findAllByexternalItemGroupNumberContainingIgnoreCaseAndServiceAreaIdIdInAndIsDeletedIsFalseAndMvnoIdIn(String externalItemGroupNumber, List<Long> serviceAreaId_id, List<Integer> mvnoId);
    List<ExternalItemManagement> findAllByexternalItemGroupNumberContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String externalItemGroupNumber, List<Integer> mvnoId);


    @Query(nativeQuery = true, value = "select count(*) from tblmexternalitemmanagement t where t.mvno_id =:mvnoId")
    Integer findTopByOrderByIdDesc(@Param("mvnoId")Integer mvnoId);
    List<ExternalItemManagement> findAllByIsDeletedIsFalseAndStatus(String status);
    List<ExternalItemManagement> findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(String status, List<Integer> mvnoId);
    List<ExternalItemManagement> findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndOwnerIdAndOwnershipType(String status, List<Integer> mvnoId, Long ownerId, String ownershipType);
    List<ExternalItemManagement> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId);
}
