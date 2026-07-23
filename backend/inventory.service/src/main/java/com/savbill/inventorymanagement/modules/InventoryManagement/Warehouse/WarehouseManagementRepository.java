package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

@JaversSpringDataAuditable
public interface WarehouseManagementRepository extends JpaRepository<WareHouse, Long>, QuerydslPredicateExecutor<WareHouse> {
	Page<WareHouse> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
	Page<WareHouse> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);
//	Page<WareHouse> findAll(Pageable pageable, List mvnoIds);

	@Query(value = "select count(*) from tblmwarehousemanagement c where c.name=:name and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from tblmwarehousemanagement c where c.name=:name and c.warehouse_id =:id and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from tblmwarehousemanagement c where c.name=:name and c.is_deleted=false", nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name") String name);

	@Query(value = "select count(*) from tblmwarehousemanagement c where c.name=:name and c.warehouse_id =:id and c.is_deleted=false", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

	@Query(nativeQuery = true, value = "select t.warehouse_id from tblmwarehousemanagement t where t.name =:name and is_deleted = false")
	Integer findId(@Param("name") String name);


	List<WareHouse> findAllByIdInAndIsDeletedIsFalse(List<Long> id);
	List<WareHouse> findAllByIdInAndIsDeletedIsFalseAndCreatedById(List<Long> id, Integer createdById);
	List<WareHouse> findAllByIsDeletedIsFalse();
	List<WareHouse> findAllByIdInAndIsDeletedIsFalseAndMvnoIdIn(List<Long> id, List<Integer> mvnoId);
	List<WareHouse> findAllByIsDeletedIsFalseAndMvnoIdAndCreatedById(Integer mvnoId, Integer createdById);
	List<WareHouse> findAllByIdInAndIsDeletedIsFalseAndMvnoIdAndCreatedById(List<Long> id, Integer mvnoId, Integer createdById);
	List<WareHouse> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId);
	List<WareHouse> findAllByIsDeletedIsFalseAndStatus(String status);
	List<WareHouse> findAllByIsDeletedIsFalseAndMvnoIdInAndStatus(List<Integer> mvnoId, String status);
	List<WareHouse> findAllByIdInAndIsDeletedIsFalseAndMvnoIdInAndStatus(List<Long> id, List<Integer> mvnoId, String status);

	@Query(value = "SELECT new WareHouse(w.id, w.name, w.warehouseType, w.address1, w.address2, w.status) FROM WareHouse w WHERE w.isDeleted = false ")
	Page<WareHouse> findAllByIsDeletedIsFalse(Pageable pageable);
	@Query(value = "SELECT new WareHouse(w.id, w.name, w.warehouseType, w.address1, w.address2, w.status) FROM WareHouse w WHERE w.isDeleted = false AND w.id IN (:id) ")
	List<WareHouse> findAllByIdInAndIsDeletedIsFalseWithoutPageable(List<Long> id);
	@Query(value = "SELECT new WareHouse(w.id, w.name, w.warehouseType, w.address1, w.address2, w.status) FROM WareHouse w WHERE w.isDeleted = false ")
	List<WareHouse> findAllByIsDeletedIsFalseWithoutPageable();
	@Query(value = "SELECT new WareHouse(w.id, w.name, w.warehouseType, w.address1, w.address2, w.status) FROM WareHouse w WHERE w.isDeleted = false AND w.id IN (:id) AND w.mvnoId IN (:mvnoId) ")
	List<WareHouse> findAllByIdInAndIsDeletedIsFalseAndMvnoIdInWithoutPageable(List<Long> id, List<Integer> mvnoId);
	@Query(value = "SELECT new WareHouse(w.id, w.name, w.warehouseType, w.address1, w.address2, w.status) FROM WareHouse w WHERE w.isDeleted = false AND w.mvnoId IN (:mvnoId) ")
	List<WareHouse> findAllByIsDeletedIsFalseAndMvnoIdInWithoutPageable(List<Integer> mvnoId);
	Page<WareHouse> findAllByIsDeletedIsFalseAndNameContainsIgnoreCase(Pageable pageable, String name);
	@Query(value = "SELECT new WareHouse(w.id, w.name, w.warehouseType, w.address1, w.address2, w.status) FROM WareHouse w WHERE w.isDeleted = false AND w.id IN (:id) AND w.mvnoId IN (:mvnoId) ")
	Page<WareHouse> findAllByIsDeletedIsFalseAndIdInAndMvnoIdIn(List<Long> id, List<Integer> mvnoId, Pageable pageable);
	Page<WareHouse> findAllByIsDeletedIsFalseAndIdInAndMvnoIdInAndNameContainsIgnoreCase(List<Long> id, List<Integer> mvnoId, String name,Pageable pageable);
	@Query(value = "SELECT new WareHouse(w.id, w.name, w.warehouseType, w.address1, w.address2, w.status) FROM WareHouse w WHERE w.isDeleted = false AND w.mvnoId IN (:mvnoId) ")
	Page<WareHouse> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId, Pageable pageable);
	Page<WareHouse> findAllByIsDeletedIsFalseAndMvnoIdInAndNameContainsIgnoreCase(List<Integer> mvnoId, String name,Pageable pageable);

	@Query(value = "SELECT new WareHouse(w.id, w.name, w.warehouseType, w.address1, w.address2, w.status) FROM WareHouse w WHERE w.id =:id")
	WareHouse findLightWarehouseById(Long id);

	@Query(value = "select t.name from tblmwarehousemanagement t where t.warehouse_id = :id and is_deleted = false", nativeQuery = true)
	String findNameById(@Param("id") Long id);

    @Query(value = "SELECT t.id FROM WareHouse t WHERE t.isDeleted = false")
    List<Long> findFilterdWarehouseIds();

    @Query(value = "SELECT t.id FROM WareHouse t WHERE t.id in (:warehouseMapIds) AND t.isDeleted = false AND t.mvnoId in (:mvnoIds)")
    List<Long> findWarehouseIdsAndMvnoIdsByWarehouseMappIds(@Param("warehouseMapIds") List<Long> warehouseMapIds, @Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "SELECT t.id FROM WareHouse t WHERE t.isDeleted = false AND t.mvnoId in (:mvnoIds)")
    List<Long> findFilterdWarehouseIdsByMvnoIds(@Param("mvnoIds") List<Integer> mvnoIds);
}
