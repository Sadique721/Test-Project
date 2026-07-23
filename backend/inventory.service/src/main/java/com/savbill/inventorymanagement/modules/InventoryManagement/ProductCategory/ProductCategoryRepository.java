package com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long>, QuerydslPredicateExecutor<ProductCategory> {

    @Query(value = "select count(*) from tblmproductcategory m where m.name=:name and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmproductcategory m where m.name=:name and m.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmproductcategory where name=:name and product_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    @Query(value = "select count(*) from tblmproductcategory where name=:name and product_id =:id and is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*)  from tbltproduct t where t.pc_id =:id and t.is_deleted =false", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    Page<ProductCategory> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);

    Page<ProductCategory> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    @Query(value = "select * from tblmproductcategory where `type` = 'CustomerBind' and status = 'Active'\n",nativeQuery = true)
    List<ProductCategory> getall();

    @Query(value = "select * from tblmproductcategory where status = 'Active' and is_deleted = false\n",nativeQuery = true)
    List<ProductCategory> getAllActiveProductCategories();

     List<ProductCategory> findAllByIdIn(Set<Long> productIds);

    Long countByNameAndIsDeletedIsFalse(String name);

    Long countByNameAndIsDeletedIsFalseAndMvnoIdIn(String name, List<Integer> mvnoId);

    Long countByNameAndIdAndIsDeletedIsFalse(String name, Long id);

    Long countByNameAndIdAndIsDeletedIsFalseAndMvnoIdIn(String name, Long id, List<Integer> mvnoId);

    Long countByIdAndIsDeletedIsFalse(Long id);

    List<ProductCategory> findAllByTypeAndStatusAndIsDeletedIsFalse(String type, String status);

    List<ProductCategory> findAllByTypeAndStatusAndIsDeletedIsFalseAndMvnoIdIn(String type, String status, List<Integer> mvnoId);

    List<ProductCategory> findAllByStatusAndIsDeletedIsFalse(String status);

    List<ProductCategory> findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(String status, List<Integer> mvnoId);

    List<ProductCategory> findAllByIsDeletedIsFalseAndStatus( String status);

    List<ProductCategory> findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn( String status, List<Integer> mvnoId);
    Page<ProductCategory> findAllByIsDeletedIsFalse(Pageable pageable);
    Page<ProductCategory> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId, Pageable pageable);
    Page<ProductCategory> findAllByIsDeletedIsFalseAndNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ProductCategory> findAllByIsDeletedIsFalseAndNameContainingIgnoreCaseAndMvnoIdIn(String name, List<Integer> mvnoId, Pageable pageable);
    Page<ProductCategory> findAllByIsDeletedIsFalseAndTypeContainingIgnoreCase(String type, Pageable pageable);
    Page<ProductCategory> findAllByIsDeletedIsFalseAndTypeContainingIgnoreCaseAndMvnoIdIn(String type, List<Integer> mvnoId, Pageable pageable);

    @Query(value = "SELECT has_mac FROM tblmproductcategory WHERE product_id = :id and is_deleted = false", nativeQuery = true)
    Boolean findHasMacById(@Param("id") Long id);

    @Query(value = "SELECT has_serial FROM tblmproductcategory WHERE product_id = :id and is_deleted = false", nativeQuery = true)
    Boolean findHasSerialById(@Param("id") Long id);

    @Query(value = "SELECT has_trackable FROM tblmproductcategory WHERE product_id = :id and is_deleted = false", nativeQuery = true)
    Boolean findHasTrackableById(@Param("id") Long id);

    @Query(value = "SELECT has_port FROM tblmproductcategory WHERE product_id = :id and is_deleted = false", nativeQuery = true)
    Boolean findHasPortById(@Param("id") Long id);

    @Query(value = "SELECT has_cas FROM tblmproductcategory WHERE product_id = :id and is_deleted = false", nativeQuery = true)
    Boolean findHasCasById(@Param("id") Long id);

    @Query(value = "SELECT unit FROM tblmproductcategory WHERE product_id = :id and is_deleted = false", nativeQuery = true)
    String findUnitById(@Param("id") Long id);

    @Query(value = "SELECT type FROM tblmproductcategory WHERE product_id = :id and is_deleted = false", nativeQuery = true)
    String findTypeById(@Param("id") Long id);

    @Query(value = "SELECT new ProductCategory(pc.hasMac, pc.hasSerial, pc.hasTrackable, pc.hasPort, pc.hasCas, " +
            "pc.unit, pc.type, pc.dtvCategory, pc.name, pc.deviceType) FROM ProductCategory pc " +
            "WHERE pc.id = :id AND pc.isDeleted = false")
    ProductCategory findProductCategoryAttributesById(@Param("id") Long id);
}
