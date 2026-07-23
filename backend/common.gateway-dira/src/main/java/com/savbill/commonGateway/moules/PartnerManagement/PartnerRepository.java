package com.savbill.commonGateway.moules.PartnerManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Integer>, QuerydslPredicateExecutor<Partner> {

    @Query(value = "select p.partnername from tblmpartners p where p.partnerid =:partnerId",nativeQuery = true)
    String findNameById(@Param("partnerId") Integer partnerId);


    @Query(value = "SELECT COUNT(*) FROM tblmpartners m WHERE m.partnerid = :partnerId AND m.partner_type = :partnerType", nativeQuery = true)
    Integer getCountByIdAndPartnerType(@Param("partnerId") Integer partnerId, @Param("partnerType") String partnerType);

    @Query("SELECT NEW com.savbill.commonGateway.moules.PartnerManagement.PartnerPojo(" +
            "p.id, p.name, p.status, p.partnerType, p.email, " +
            "p.parentPartner.id, p.isDelete, p.mvnoId, p.buId) " +
            "FROM Partner p " +
            "LEFT JOIN PartnerServiceAreaMapping psam ON p.id = psam.partnerId " +
            "WHERE p.isDelete = false " +
            "AND p.status = 'ACTIVE' " +
            "AND (:mvnoId = 1 OR p.mvnoId IN (:mvnoId, 1)) " +
            "AND (:buListSize = 0 OR p.buId IN (:buIds)) " +
            "AND (:hasServiceAreaIds = false OR psam.serviceId IN (:serviceAreaIds)) " +
            "AND ( " +
            "     (:isLco = true AND p.partnerType = 'LCO') " +
            "  OR (:isLco = false AND (p.partnerType != 'LCO' OR p.partnerType IS NULL)) " +
            ")")
    List<PartnerPojo> findFilteredPartners(
            @Param("mvnoId") Integer mvnoId,
            @Param("buIds") List<Long> buIds,
            @Param("buListSize") int buListSize,
            @Param("serviceAreaIds") List<Integer> serviceAreaIds,
            @Param("hasServiceAreaIds") boolean hasServiceAreaIds,
            @Param("isLco") boolean isLco
    );



    @Query("SELECT NEW com.savbill.commonGateway.moules.PartnerManagement.PartnerPojo(" +
            "p.id, p.name, p.status, p.partnerType, p.email, " +
            "p.parentPartner.id, p.isDelete, p.mvnoId, p.buId) " +
            "FROM Partner p " +
            "LEFT JOIN PartnerServiceAreaMapping psam ON p.id = psam.partnerId " +
            "WHERE p.isDelete = false " +
            "AND p.status = 'ACTIVE' " +
            "AND (:mvnoId = 1 OR p.mvnoId IN (:mvnoId, 1)) " +
            "AND (:hasServiceAreaIds = false OR psam.serviceId IN (:serviceAreaIds)) " )
    List<PartnerPojo> getAllPartner(
            @Param("mvnoId") Integer mvnoId,
            @Param("serviceAreaIds") List<Integer> serviceAreaIds,
            @Param("hasServiceAreaIds") boolean hasServiceAreaIds
    );


}
