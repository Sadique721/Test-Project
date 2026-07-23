package com.savbill.inventorymanagement.modules.InventoryManagement.Inward;

import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.Outward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@JaversSpringDataAuditable
public interface InwardRepository extends JpaRepository<Inward, Long>, QuerydslPredicateExecutor<Inward> {

    Inward findByRmsInwardId(String rmsInwardId);

    //List<Inward> findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(String inwardNumber, List<Long> destinationIds, String destinationType);
    List<Inward> findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(String inwardNumber, List<Long> destinationIds, String destinationType, List<Integer> mvno_ids);

    List<Inward> findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(String inwardNumber, List<Long> destinationIds, String destinationType);

    @Query(nativeQuery = true, value = "select t.inward_id from tblminward t where t.outward_id =:id and t.is_deleted = false")
    Long findInwardIdByOutwardId(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select t.approval_status from tblminward t where t.inward_id =:id and t.is_deleted = false")
    String findApprovalStatusByInwardId(@Param("id") Long id);

    @Query(nativeQuery = true, value = "select * from tblminward t where t.product_id =:id")
    List<Inward> findAllByProductId(@Param("id") Integer id);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblmoutward t1 where t1.inward_id =:id and t1.is_deleted =false\n" +
            "union all\n" +
            "select count(*) as tab from tbltcustomerinventorymapping t2 where t2.inward_id =:id and t2.is_deleted =false\n" +
            "union all\n" +
            "select count(*) as tab from tblmnetworkdevices t3 where t3.inward_id =:id and t3.is_deleted =false\n" +
            ")tbl", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    Page<Inward> findAllByIdIn(List<Long> ids, Pageable pageable);

    //List<Inward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(List<Long> destinationIds, String destinationType);
    List<Inward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(List<Long> destinationIds, String destinationType, List<Integer> mvno_ids);

    List<Inward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(List<Long> destinationIds, String destinationType);

    Page<Inward> findAllByinwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(String inwardNumber, Pageable pageable);

    List<Inward> findAllByinwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(String inwardNumber);

    @Query(nativeQuery = true, value = "select count(*) from tblminward t where t.mvno_id =:mvnoId")
    Integer findTopByOrderByIdDesc(@Param("mvnoId") Integer mvnoId);


    @Query(value = "select count(*) as tab from tblminward t  where t.destination_id =:warehouseid", nativeQuery = true)
    Integer deleteVerifyWareHouse(@Param("warehouseid") Integer warehouseId);

    List<Inward> findAllByIdIn(List<Long> ids);

    Long countByProductIdAndIsDeletedFalse(Product product);

    List<Inward> findAllByApprovalStatusAndOutwardIdIsNullAndProductIdInAndIsDeletedFalse(String status, List<Product> productIds);


    @Query(nativeQuery = true, value = "SELECT * FROM tblminward t WHERE t.outward_id IS NULL AND t.approval_status='Approve' AND warrantystartdate IS NOT NULL AND warrantyenddate IS NOT NULL")
    List<Inward> findAllByApprovalStatusAndWarrantyStartDateAndOutwardIdIsNull();

    @Query(nativeQuery = true, value = "SELECT * FROM tblminward t WHERE  t.approval_status='Approve' AND warrantystartdate IS NOT NULL AND warrantyenddate IS NOT NULL")
    List<Inward> findAllByApprovalStatusAndWarrantyStartDate();

    @Query(nativeQuery = true, value = "SELECT * FROM tblminward t WHERE t.inward_number=:inwardNumber AND t.destination_id IS NOT NULL AND t.destination_id NOT IN :destinationIds AND t.destination_type=:destinationType AND t.createdbystaffid IN :destinationIds")
    List<Inward> findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn1(@Param("inwardNumber") String inwardNumber, @Param("destinationIds") List<Long> destinationIds, @Param("destinationType") String destinationType);

    @Query(nativeQuery = true, value = "SELECT * FROM tblminward t WHERE t.destination_id IS NOT NULL AND t.destination_id NOT IN :destinationIds AND t.destination_type=:destinationType AND t.createdbystaffid IN :destinationIds")
    List<Inward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn1(@Param("destinationIds") List<Long> destinationIds, @Param("destinationType") String destinationType);

    //todo used when record groups view were not there
    @Query("SELECT new Inward(i.id, i.inwardNumber, i.productId, i.type, i.qty, i.inTransitQty, i.status, i.approvalStatus,i.createdByName,i.destinationType,i.createdById,i.destinationId,i.totalMacSerial,i.outwardId.id) " +
            "FROM Inward i WHERE i.id IN :ids and i.isDeleted = false order by i.id desc")
    Page<Inward> findLightInwardByIds(List<Long> ids, Pageable pageable);


    @Query("SELECT new Inward(i.id, i.inwardNumber, i.productId, i.type, i.qty, i.inTransitQty, i.status, i.approvalStatus, i.createdByName, i.destinationType, i.createdById, i.destinationId, i.totalMacSerial, i.outwardId.id, i.groupId, i.isGroup) " +
            "FROM Inward i WHERE i.id IN :ids AND i.groupId IS NULL AND i.isDeleted = false ORDER BY i.id DESC")
    Page<Inward> findLightInwardByIdsAndGroup(List<Long> ids, Pageable pageable);

    @Query("SELECT new Inward(i.id, i.inwardNumber, i.productId, i.type, i.qty, i.inTransitQty, " +
            "i.status, i.approvalStatus,i.createdByName,i.destinationType,i.createdById,i.destinationId,i.totalMacSerial, i.outwardId.id) " +
            "FROM Inward i " +
            "WHERE LOWER(i.inwardNumber) LIKE LOWER(CONCAT('%', :inwardNumber, '%')) " +
            "AND i.destinationId IN :destinationIds " +
            "AND i.destinationType = :destinationType " +
            "AND i.isDeleted = false " +
            "AND i.mvnoId IN :mvnoIds")
    List<Inward> findLightInwardByFilters(
            @Param("inwardNumber") String inwardNumber,
            @Param("destinationIds") List<Long> destinationIds,
            @Param("destinationType") String destinationType,
            @Param("mvnoIds") List<Integer> mvnoIds
    );

    @Query("SELECT new Inward(i.id, i.inwardNumber, i.productId, i.type, i.qty, i.inTransitQty, " +
            "i.status, i.approvalStatus,i.createdByName,i.destinationType,i.createdById,i.destinationId,i.totalMacSerial, i.outwardId.id) " +
            "FROM Inward i " +
            "WHERE i.destinationId IN :destinationIds " +
            "AND i.destinationType = :destinationType " +
            "AND i.isDeleted = false " +
            "AND i.mvnoId IN :mvnoIds")
    List<Inward> findLightInwardByDestinationAndMvno(
            @Param("destinationIds") List<Long> destinationIds,
            @Param("destinationType") String destinationType,
            @Param("mvnoIds") List<Integer> mvnoIds
    );

    @Query("SELECT new Inward(i.id, i.inwardNumber, i.productId, i.type, i.qty, i.inTransitQty, " +
            "i.status, i.approvalStatus,i.createdByName,i.destinationType,i.createdById,i.destinationId,i.totalMacSerial, i.outwardId.id) " +
            "FROM Inward i " +
            "WHERE LOWER(i.inwardNumber) LIKE LOWER(CONCAT('%', :inwardNumber, '%')) " +
            "AND i.destinationId IN :destinationIds " +
            "AND i.destinationType = :destinationType " +
            "AND i.isDeleted = false")
    List<Inward> findLightInwardByInwardNumberAndDestination(
            @Param("inwardNumber") String inwardNumber,
            @Param("destinationIds") List<Long> destinationIds,
            @Param("destinationType") String destinationType
    );

    @Query("SELECT new Inward(i.id, i.inwardNumber, i.productId, i.type, i.qty, i.inTransitQty, " +
            "i.status, i.approvalStatus,i.createdByName,i.destinationType,i.createdById,i.destinationId,i.totalMacSerial, i.outwardId.id) " +
            "FROM Inward i " +
            "WHERE i.destinationId IN :destinationIds " +
            "AND i.destinationType = :destinationType " +
            "AND i.isDeleted = false")
    List<Inward> findLightInwardByDestination(
            @Param("destinationIds") List<Long> destinationIds,
            @Param("destinationType") String destinationType
    );

    @Query("SELECT new Inward(i.id, i.inwardNumber, i.productId, i.type, i.qty, i.inTransitQty, i.status, i.approvalStatus,i.createdByName,i.destinationType,i.createdById,i.destinationId,i.totalMacSerial, i.outwardId.id) " +
            "FROM Inward i " +
            "WHERE i.inwardNumber = :inwardNumber " +
            "AND i.destinationId IS NOT NULL " +
            "AND i.destinationId NOT IN :destinationIds " +
            "AND i.destinationType = :destinationType " +
            "AND i.createdById IN :destinationIds")
    List<Inward> findAllLightInwardByInwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn1(
            @Param("inwardNumber") String inwardNumber,
            @Param("destinationIds") List<Long> destinationIds,
            @Param("destinationType") String destinationType);

    @Query("SELECT new Inward(i.id, i.inwardNumber, i.productId, i.type, i.qty, i.inTransitQty, i.status, i.approvalStatus,i.createdByName,i.destinationType,i.createdById,i.destinationId,i.totalMacSerial, i.outwardId.id) " +
            "FROM Inward i " +
            "WHERE i.destinationId IS NOT NULL " +
            "AND i.destinationId NOT IN :destinationIds " +
            "AND i.destinationType = :destinationType " +
            "AND i.createdById IN :destinationIds")
    List<Inward> findAllLightInwardByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn1(
            @Param("destinationIds") List<Long> destinationIds,
            @Param("destinationType") String destinationType);


    @Query(value = "SELECT t.inward_id FROM tblminward t where t.approval_status = :approvalStatus and t.is_deleted =false and t.mvno_id in :mvnoIds and t.destination_type = :sourcetype and t.destination_id =:sourceid and t.product_id = :productid", nativeQuery = true)
    List<Long> findInwardIdsByApprovalStatusAndMvnoIdAndDestinationTypeAndDestinationIdAndProductId(@Param("approvalStatus") String approvalStatus, @Param("mvnoIds") List<Integer> mvnoIds, @Param("sourcetype") String sourcetype, @Param("sourceid") Long sourceid, @Param("productid") Long productid);

    @Query(value = "SELECT t.inward_id FROM tblminward t where t.approval_status = :approvalStatus and t.is_deleted =false and t.destination_type = :sourcetype and t.destination_id =:sourceid and t.product_id = :productid", nativeQuery = true)
    List<Long> findInwardIdsByApprovalStatusAndDestinationTypeAndDestinationIdAndProductId(@Param("approvalStatus") String approvalStatus, @Param("sourcetype") String sourcetype, @Param("sourceid") Long sourceid, @Param("productid") Long productid);

    @Query(value = "SELECT t.outward_id FROM tblminward t WHERE t.inward_id = :inwardId and t.approval_status = :approvalStatus and t.is_deleted =false and t.status = :status", nativeQuery = true)
    Long findOutwardIdsByInwardIdAndApprovalStatusAndStatus(@Param("inwardId") Long inwardId, @Param("approvalStatus") String approvalStatus, @Param("status") String status);

    @Query(value = "SELECT t.outward_id FROM tblminward t WHERE t.inward_id = :inwardId and t.approval_status = :approvalStatus and t.is_deleted =false and t.status = :status and t.mvno_id in :mvnoIds", nativeQuery = true)
    Long findOutwardIdsByInwardIdAndApprovalStatusAndStatusAndMvnoIds(@Param("inwardId") Long inwardId, @Param("approvalStatus") String approvalStatus, @Param("status") String status, @Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "SELECT t.in_transit_qty FROM tblminward t WHERE t.inward_id = :inwardId and t.approval_status = :approvalStatus and t.is_deleted =false and t.status = :status", nativeQuery = true)
    Long findInTransitQuantityByInwardIdAndApprovalStatusAndStatus(@Param("inwardId") Long inwardId, @Param("approvalStatus") String approvalStatus, @Param("status") String status);

    @Query(value = "SELECT t.in_transit_qty FROM tblminward t WHERE t.inward_id = :inwardId and t.is_deleted =false", nativeQuery = true)
    Long findInTransitQuantityByInwardId(@Param("inwardId") Long inwardId);

    @Query(value = "SELECT t.destination_type FROM tblminward t WHERE t.inward_id = :inwardId and t.is_deleted =false", nativeQuery = true)
    String findDestinationTypeByInwardId(@Param("inwardId") Long inwardId);

    @Query(value = "SELECT t.destination_id FROM tblminward t WHERE t.inward_id = :inwardId and t.is_deleted =false", nativeQuery = true)
    Long findDestinationIdByInwardId(@Param("inwardId") Long inwardId);

    @Query(value = "SELECT * FROM tblminward i " +
            "WHERE i.outward_id = :outwardId " +
            "AND i.status = :activeStatus " +
            "AND i.is_deleted = false " +
            "AND (i.mvno_id = 1 OR :currentMvnoId = 1 OR i.mvno_id = :currentMvnoId)" +
            "LIMIT 1", nativeQuery = true)
    Inward findInwardByOutwardId(
            @Param("outwardId") Long outwardId,
            @Param("activeStatus") String activeStatus,
            @Param("currentMvnoId") Integer currentMvnoId
    );

    @Query(value = "SELECT t.source_type FROM tblminward t WHERE t.inward_id = :inwardId and t.is_deleted =false", nativeQuery = true)
    String findSourceTypeByInwardId(@Param("inwardId") Long inwardId);

    @Query(value = "SELECT t.source_id FROM tblminward t WHERE t.inward_id = :inwardId and t.is_deleted =false", nativeQuery = true)
    Long findSourceIdByInwardId(@Param("inwardId") Long inwardId);

    @Query(value = "SELECT t.product_id FROM tblminward t WHERE t.inward_id = :inwardId and t.is_deleted =false", nativeQuery = true)
    Long findProductIdByInwardId(@Param("inwardId") Long inwardId);


    @Modifying
    @Query(value = "UPDATE tblminward SET total_mac_serial = total_mac_serial + :incrementValue " +
            "WHERE is_deleted = false AND status = :activeStatus AND outward_id = :outwardId",
            nativeQuery = true)
    int updateTotalMacSerial(@Param("outwardId") Long outwardId,
                             @Param("incrementValue") int incrementValue,
                             @Param("activeStatus") String activeStatus);


    @Query(value = "SELECT inward_id FROM tblminward " +
            "WHERE is_deleted = false " +
            "AND status = :status " +
            "AND outward_id = :outwardId " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Long> findInwardIdByOutwardId(@Param("status") String status,
                                           @Param("outwardId") Long outwardId);
    @Query(value = "SELECT destination_id FROM tblminward " +
            "WHERE is_deleted = false " +
            "AND status = :status " +
            "AND outward_id = :outwardId " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Long> findDestinationIdByOutwardId(@Param("status") String status,
                                                @Param("outwardId") Long outwardId);
    @Query(value = "SELECT destination_type FROM tblminward " +
            "WHERE is_deleted = false " +
            "AND status = :status " +
            "AND outward_id = :outwardId " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<String> findDestinationTypeByOutwardId(@Param("status") String status,
                                                    @Param("outwardId") Long outwardId);

    @Query(value = "SELECT i.in_transit_qty FROM tblminward i " +
            "WHERE i.outward_id = :outwardId " +
            "AND i.status = :activeStatus " +
            "AND i.is_deleted = false " +
            "AND (i.mvno_id = 1 OR :currentMvnoId = 1 OR i.mvno_id = :currentMvnoId)" +
            "LIMIT 1", nativeQuery = true)
    Long findInTransitQuantityByOutwardId(
            @Param("outwardId") Long outwardId,
            @Param("activeStatus") String activeStatus,
            @Param("currentMvnoId") Integer currentMvnoId
    );

    @Query(value = "SELECT i.inward_id FROM tblminward i " +
            "WHERE i.outward_id = :outwardId " +
            "AND i.status = :activeStatus " +
            "AND i.is_deleted = false " +
            "AND (i.mvno_id = 1 OR :currentMvnoId = 1 OR i.mvno_id = :currentMvnoId)" +
            "LIMIT 1", nativeQuery = true)
    Optional<Long> findInwardIdByOutwardIdAndStatus(
            @Param("outwardId") Long outwardId,
            @Param("activeStatus") String activeStatus,
            @Param("currentMvnoId") Integer currentMvnoId
    );

    @Query("SELECT new Inward(t.sourceType, t.sourceId, t.destinationId, t.destinationType, t.inTransitQty) FROM Inward t WHERE t.id = :inwardId and t.isDeleted = false")
    Inward findInwardDetailsByInwardId(@Param("inwardId") Long inwardId);

    @Query("SELECT o FROM Inward o WHERE o.id = :id OR o.groupId = :id ORDER BY o.id ASC")
    List<Inward> findParentWithChildren(@Param("id") Long id);


    @Query("SELECT i FROM Inward i WHERE i.id = :id OR i.groupId = :id")
    List<Inward> findByGroupIdOrId(@Param("id") Long id);
}
