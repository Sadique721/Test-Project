package com.savbill.inventorymanagement.modules.InventoryManagement.Outward;

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
public interface OutwardRepository  extends JpaRepository<Outward, Long>, QuerydslPredicateExecutor<Outward> {
	Page<Outward> findAllByoutwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(String outwardNumber, Pageable pageable);
	List<Outward> findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(String outwardNumber,List<Long> destinationIds, String destinationType, List<Integer> mvno_ids);
	List<Outward> findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(String outwardNumber,List<Long> destinationIds, String destinationType);
	@Query(value = "select * from tblmoutward t WHERE t.is_delete = false", nativeQuery = true
            , countQuery = "select count(*) from tblmoutward t WHERE t.is_delete = false")
    Page<Outward> findAll(Pageable pageable);
	Page<Outward> findAllByoutwardNumberContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String outwardNumber, Pageable pageable, List mvnoIds);

	@Query(value = 	"select count(*) from tblmoutward t1 where t1.inward_id =:id and t1.is_deleted =false", nativeQuery = true)
	Integer deleteVerify(@Param("id") Integer id);

	Page<Outward> findAllByIdInAndGroupIdIsNull(List<Long> ids, Pageable pageable);
	Page<Outward> findAllByIdIn(List<Long> ids, Pageable pageable);
	List<Outward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(List<Long> destinationIds, String destinationType, List<Integer> mvno_ids);
	List<Outward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(List<Long> destinationIds, String destinationType);

	@Query(nativeQuery = true, value = "select count(*) from tblmoutward t where t.mvno_id =:mvnoId")
	Integer findTopByOrderByIdDesc(@Param("mvnoId")Integer mvnoId);

	Outward findByRequestInventoryProductId(Long id);

	List<Outward> findAllByIdIn(List<Long> ids);
	List<Outward> findAllByIsDeletedIsFalseAndStatus(String status);

	List<Outward> findAllByIsDeletedIsFalseAndCreatedByIdAndMvnoIdInAndOutwardNumber(Integer createdById, List<Integer> mvnoId, String outwardNumber);
	List<Outward> findAllByIsDeletedIsFalseAndCreatedByIdAndOutwardNumber(Integer createdById, String outwardNumber);
	List<Outward> findAllByIsDeletedIsFalseAndCreatedByIdAndMvnoIdIn(Integer createdById, List<Integer> mvnoId);
	List<Outward> findAllByIsDeletedIsFalseAndCreatedById(Integer createdById);
	@Query("SELECT new Outward(o.id, o.outwardNumber, o.destinationType, o.sourceType, o.status, \n" +
			"  o.approvalStatus, o.inTransitQty, o.createdByName, o.destinationId, o.sourceId, \n" +
			"  o.selectedItems) " +
			"FROM Outward o " +
			"WHERE LOWER(o.outwardNumber) LIKE LOWER(CONCAT('%', :outwardNumber, '%')) " +
			"AND o.destinationId IN (:destinationIds) " +
			"AND o.destinationType = :destinationType " +
			"AND o.isDeleted = false " +
			"AND o.mvnoId IN (:mvnoIds)")
	List<Outward> findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(
			@Param("outwardNumber") String outwardNumber,
			@Param("destinationIds") List<Long> destinationIds,
			@Param("destinationType") String destinationType,
			@Param("mvnoIds") List<Integer> mvnoIds);

	@Query("SELECT new Outward(o.id, o.outwardNumber, o.destinationType, o.sourceType, o.status, \n" +
			"  o.approvalStatus, o.inTransitQty, o.createdByName, o.destinationId, o.sourceId, \n" +
			"  o.selectedItems) " +
			"FROM Outward o " +
			"WHERE o.destinationId IN (:destinationIds) " +
			"AND o.destinationType = :destinationType " +
			"AND o.isDeleted = false " +
			"AND o.mvnoId IN (:mvnoIds)")
	List<Outward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(
			@Param("destinationIds") List<Long> destinationIds,
			@Param("destinationType") String destinationType,
			@Param("mvnoIds") List<Integer> mvnoIds);

	@Query("SELECT new Outward(o.id, o.outwardNumber, o.destinationType, o.sourceType, o.status, \n" +
			"  o.approvalStatus, o.inTransitQty, o.createdByName, o.destinationId, o.sourceId, \n" +
			"  o.selectedItems) " +
			"FROM Outward o " +
			"WHERE LOWER(o.outwardNumber) LIKE LOWER(CONCAT('%', :outwardNumber, '%')) " +
			"AND o.destinationId IN (:destinationIds) " +
			"AND o.destinationType = :destinationType " +
			"AND o.isDeleted = false")
	List<Outward> findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(
			@Param("outwardNumber") String outwardNumber,
			@Param("destinationIds") List<Long> destinationIds,
			@Param("destinationType") String destinationType);

	@Query("SELECT new Outward(o.id, o.outwardNumber, o.destinationType, o.sourceType, o.status, \n" +
			"  o.approvalStatus, o.inTransitQty, o.createdByName, o.destinationId, o.sourceId, \n" +
			"  o.selectedItems) " +
			"FROM Outward o " +
			"WHERE o.destinationId IN (:destinationIds) " +
			"AND o.destinationType = :destinationType " +
			"AND o.isDeleted = false")
	List<Outward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(
			@Param("destinationIds") List<Long> destinationIds,
			@Param("destinationType") String destinationType);

	@Query("SELECT new Outward(o.id, o.outwardNumber, o.destinationType, o.sourceType, o.status, \n" +
			"  o.approvalStatus, o.inTransitQty, o.createdByName, o.destinationId, o.sourceId, \n" +
			"  o.selectedItems) " +
			"FROM Outward o " +
			"WHERE o.isDeleted = false " +
			"AND o.createdById = :createdById " +
			"AND o.mvnoId IN (:mvnoId) " +
			"AND o.outwardNumber = :outwardNumber")
	List<Outward> findAllByIsDeletedIsFalseAndCreatedByIdAndMvnoIdInAndOutwardNumberWithConstructor(
			@Param("createdById") Integer createdById,
			@Param("mvnoId") List<Integer> mvnoId,
			@Param("outwardNumber") String outwardNumber);

	@Query("SELECT new Outward(o.id, o.outwardNumber, o.destinationType, o.sourceType, o.status, \n" +
			"  o.approvalStatus, o.inTransitQty, o.createdByName, o.destinationId, o.sourceId, \n" +
			"  o.selectedItems) " +
			"FROM Outward o " +
			"WHERE o.isDeleted = false " +
			"AND o.createdById = :createdById " +
			"AND o.mvnoId IN (:mvnoIds)")
	List<Outward> findAllByIsDeletedIsFalseAndCreatedByIdAndMvnoIdInWithConstructor(
			@Param("createdById") Integer createdById,
			@Param("mvnoIds") List<Integer> mvnoIds);

	@Query("SELECT new Outward(o.id, o.outwardNumber, o.destinationType, o.sourceType, o.status, \n" +
			"  o.approvalStatus, o.inTransitQty, o.createdByName, o.destinationId, o.sourceId, \n" +
			"  o.selectedItems) " +
			"FROM Outward o " +
			"WHERE o.isDeleted = false " +
			"AND o.createdById = :createdById " +
			"AND o.outwardNumber = :outwardNumber")
	List<Outward> findAllByIsDeletedIsFalseAndCreatedByIdAndOutwardNumberWithConstructor(
			@Param("createdById") Integer createdById,
			@Param("outwardNumber") String outwardNumber);

	@Query("SELECT new Outward(o.id, o.outwardNumber, o.destinationType, o.sourceType, o.status, \n" +
			"  o.approvalStatus, o.inTransitQty, o.createdByName, o.destinationId, o.sourceId, \n" +
			"  o.selectedItems) " +
			"FROM Outward o " +
			"WHERE o.isDeleted = false " +
			"AND o.createdById = :createdById")
	List<Outward> findAllByIsDeletedIsFalseAndCreatedByIdWithConstructor(
			@Param("createdById") Integer createdById);

	@Query("SELECT o.id FROM Outward o WHERE o.isDeleted = false AND o.status = :status " +
			"AND o.productId.id = :productId AND o.approvalStatus = :approvalStatus")
	List<Long> findOutwardIds(@Param("status") String status,
							  @Param("productId") Long productId,
							  @Param("approvalStatus") String approvalStatus);

    @Query(value = "SELECT o.source_type FROM tblmoutward o WHERE o.is_deleted = false and o.outward_id = :outwardId", nativeQuery = true)
    String findSourceTypeByOutwardId(@Param("outwardId") Long outwardId);


    @Query(value = "SELECT o.source_id FROM tblmoutward o WHERE o.is_deleted = false and o.outward_id = :outwardId", nativeQuery = true)
    Long findSourceIdByOutwardId(@Param("outwardId") Long outwardId);

    @Query(value = "SELECT new Outward(o.sourceType, o.sourceId) FROM Outward o WHERE o.isDeleted = false and o.id = :outwardId")
    Outward findSourceAndTypeByOutwardId(@Param("outwardId") Long outwardId);

    @Query(value = " SELECT o.in_transit_qty FROM tblmoutward o " +
            "WHERE o.outward_id = :outwardId " +
            "AND o.is_deleted = false " +
            "AND o.status = :activeStatus " +
            "AND (o.mvno_id = 1 OR :currentMvnoId = 1 OR o.mvno_id = :currentMvnoId) " +
            "LIMIT 1", nativeQuery = true)
    Long findInTransitQuantityByOutwardId(@Param("outwardId") Long outwardId,
                                           @Param("activeStatus") String activeStatus,
                                           @Param("currentMvnoId") Integer currentMvnoId);


    @Query(value = "SELECT o.product_id FROM tblmoutward o WHERE o.is_deleted = false and o.outward_id = :outwardId", nativeQuery = true)
    Long findProductIdByOutwardId(@Param("outwardId") Long outwardId);


	@Query("SELECT o FROM Outward o WHERE o.id = :id OR o.groupId = :id ORDER BY o.id ASC")
	List<Outward> findParentWithChildren(@Param("id") Long id);
}

