package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ItemSkippedRepository extends JpaRepository<ItemSkipped, Long>, QuerydslPredicateExecutor<ItemSkipped> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ItemSkipped i WHERE i.inwardId = :inwardId")
    void deleteByInwardId(@Param("inwardId") Long inwardId);

    @Query("SELECT new com.savbill.inventorymanagement.modules.InventoryManagement.Item.GetRemarksDTO(r.imsi, r.iccid, r.pin1, r.puk1, r.pin2, r.puk2, r.kiEncrypted, r.acc, r.adm, r.kic, r.kid, r.kik, r.reason, r.mac, r.trackable, r.port, r.serial, r.msisdn) " +
            "FROM ItemSkipped r " +
            "WHERE r.inwardId = :inwardId AND r.mvnoId = :mvnoId")
    Page<GetRemarksDTO> findRemarksByInwardId(@Param("inwardId") Long inwardId, @Param("mvnoId") Long mvnoId, Pageable pageable);

//    @Query("SELECT new com.savbill.inventorymanagement.modules.InventoryManagement.Item.GetRemarksDTO(r.imsi, r.iccid, r.pin1, r.puk1, r.pin2, r.puk2, r.kiEncrypted, r.acc, r.adm, r.kic, r.kid, r.kik, r.reason, r.mac, r.trackable, r.port, r.serial, r.msisdn) " +
//            "FROM ItemSkipped r " +
//            "WHERE r.outwardId = :outwardId AND r.mvnoId = :mvnoId")
//    Page<GetRemarksDTO> findRemarksByOutwardId(@Param("outwardId") Long outwardId, @Param("mvnoId") Long mvnoId, Pageable pageable);

    @Query("SELECT new com.savbill.inventorymanagement.modules.InventoryManagement.Item.GetRemarksDTO(" +
            "r.imsi, r.iccid, r.pin1, r.puk1, r.pin2, r.puk2, r.kiEncrypted, r.acc, r.adm, r.kic, r.kid, r.kik, r.reason, r.mac, r.serial, r.port, r.trackable, r.msisdn) " +
            "FROM ItemSkipped r " +
            "WHERE r.outwardId = :outwardId AND r.mvnoId = :mvnoId")
    Page<GetRemarksDTO> findRemarksByOutwardId(@Param("outwardId") Long outwardId,
                                               @Param("mvnoId") Long mvnoId,
                                               Pageable pageable);
    @Modifying
    @Transactional
    @Query("DELETE FROM ItemSkipped i WHERE i.outwardId = :outwardId")
    void deleteByOutwardId(@Param("outwardId") Long outwardId);

    List<ItemSkipped> findByInwardId(Long inwardId);
    List<ItemSkipped> findByOutwardId(Long outwardId);
}
