package com.savbill.cpm.modules.InventoryManagement.ExternalItemMacSerialMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalItemMacSerialMappingRepo extends JpaRepository<ExternalItemMacSerialMapping, Long>, QuerydslPredicateExecutor<ExternalItemMacSerialMapping> {
//    @Query(value = "select * from tbltexternalitemmacmapping t where t.external_item_id in :id and t.mac in :mac and t.is_deleted =false", nativeQuery = true)
//    List<ExternalItemMacSerialMapping> findByCurrentExternalItemId(@Param("id") List<Long> id, @Param("mac") List<String> mac);
//
//    @Query(value = "select * from tbltexternalitemmacmapping t where t.item_id in :id and t.is_deleted =false", nativeQuery = true)
//    List<ExternalItemMacSerialMapping> findAllByItemId(@Param("id") List<Long> id);

}
