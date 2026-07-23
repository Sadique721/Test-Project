package com.savbill.inventorymanagement.modules.InventoryManagement.Product;

import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
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
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {

	Product findByName(String productDto);
	Page<Product> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
	Page<Product> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

	Page<Product> findAllByIsDeletedIsFalse(Pageable pageable);
	Page<Product> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId, Pageable pageable);
	List<Product> findAllByIsDeletedIsFalse();
	List<Product> findAllByIsDeletedIsFalseAndMvnoId(Integer mvnoId);
	@Query(value = "select count(*) from tbltproduct m where m.name=:name and m.is_deleted=false",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name);

	@Query(value = "select count(*) from tbltproduct m where m.name=:name and m.is_deleted=false and mvno_id in :mvnoIds",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);
	@Query(value = "select count(*) from tbltproduct m where m.rms_product_id=:productId and m.is_deleted=false",nativeQuery = true)
	Integer duplicateProductIdVerifyAtSave(@Param("productId")String productId);

	@Query(value = "select count(*) from tbltproduct m where m.rms_product_id=:productId and m.is_deleted=false and mvno_id in :mvnoIds",nativeQuery = true)
	Integer duplicateProductIdVerifyAtSave(@Param("productId")String productId, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from tblminward t where t.product_id =:id and t.is_deleted=false" ,nativeQuery = true)
	Integer deleteVerify(@Param("id")Integer id);

	@Query(value = "select count(*) from tbltproduct t where t.product_id =:id and t.name =:name and t.is_deleted =false", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id);

	// Find duplicate pop name at edit with mvnoId
	@Query(value = "select count(*) from tbltproduct t where t.product_id =:id and  t.name =:name and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);
	@Query(value = "select count(*) from tbltproduct t where t.product_id =:id and t.rms_product_id =:productId and t.is_deleted =false", nativeQuery = true)
	Integer duplicateProductIdVerifyAtEdit(@Param("productId")String productId, @Param("id") Integer id);

	// Find duplicate pop name at edit with mvnoId
	@Query(value = "select count(*) from tbltproduct t where t.product_id =:id and  t.name =:productId and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateProductIdVerifyAtEdit(@Param("productId")String productId, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);

	List<Product> findAllByIdIn(List<Long> id);

	@Query(value = "select count(*) from tbltproduct t where t.case_id =:id and t.is_deleted =false", nativeQuery = true)
	Integer countAllByByCasId(Integer id);

	List<Product> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name);


	@Query(value = "select count(*) as tab from tbltproduct t  where t.vendorid =:vendorid" ,nativeQuery = true)
	Integer deleteVerifyVendor(@Param("vendorid")Integer vendorId);

	Long countByNameAndIsDeletedIsFalse(String name);
	Long countByNameAndIsDeletedIsFalseAndMvnoIdIn(String name, List<Integer> mvnoId);
	Long countByNameAndIdAndIsDeletedIsFalse(String name, Long id);
	Long countByNameAndIdAndIsDeletedIsFalseAndMvnoIdIn(String name, Long id, List<Integer> mvnoId);
	Long countByVendorIdAndIsDeletedIsFalse(Long vendorId);
	Long countByProductCategoryIdAndIsDeletedIsFalse(Long productCategory_id);
	List<Product> findAllByStatusAndIsDeletedIsFalse(String status);
	List<Product> findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(String status, List<Integer> mvnoId);
	List<Product> findAllByStatusAndIsDeletedIsFalseAndIdIn(String status, List<Long> id);
	List<Product> findAllByStatusAndIsDeletedIsFalseAndIdInAndMvnoIdIn(String status, List<Long> id, List<Integer> mvnoId);
	Long countByProductIdAndIsDeletedFalse(String productId);
	Long countByProductIdAndIsDeletedFalseAndMvnoIdIn(String productId, List<Integer> mvnoId);
	Long countByProductIdAndIsDeletedFalseAndId(String productId, Long id);
	Long countByProductIdAndIsDeletedFalseAndMvnoIdInAndId(String productId, List<Integer> mvnoId, Long id);
	List<Product> findAllByStatusAndProductCategoryAndIsDeletedIsFalse(String status,ProductCategory productCategory);
    @Query(value = "SELECT p.pc_id FROM tblmproduct p WHERE p.product_id = :productId and p.is_deleted=false", nativeQuery = true)
    Long findProductCategoryIdByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT p.name FROM tblmproduct p WHERE p.product_id = :productId and p.is_deleted=false", nativeQuery = true)
    String findProductNameByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT p.expiry_time_unit FROM tblmproduct p WHERE p.product_id = :productId and p.is_deleted=false", nativeQuery = true)
    String findExpiryTimeUnitByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT p.expiry_time FROM tblmproduct p WHERE p.product_id = :productId and p.is_deleted=false", nativeQuery = true)
    Integer findExpiryTimeByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT new Product(p.id, p.name) FROM Product p WHERE p.id = :productId")
    Product findByIdAndIsDeletedIsFalse(@Param("productId") Long productId);

    @Query(value = "SELECT p.isoemconsider from tblmproduct p WHERE p.product_id = :productId AND p.is_deleted=false", nativeQuery = true)
    Boolean findIsoemConsiderByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT p.isassetconsider from tblmproduct p WHERE p.product_id = :productId AND p.is_deleted=false", nativeQuery = true)
    Boolean findHasAssetConsiderByProductId(@Param("productId") Long productId);
}
