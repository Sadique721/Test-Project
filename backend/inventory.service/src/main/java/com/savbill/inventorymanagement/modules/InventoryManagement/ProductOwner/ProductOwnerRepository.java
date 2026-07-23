package com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOwnerRepository extends JpaRepository<ProductOwner, Long>, QuerydslPredicateExecutor<ProductOwner> {
    @Query(value = "select * from tblmproductowner m where m.product_id=:productId and m.owner_id=:ownerId and owner_type =:ownerType",nativeQuery = true)
    ProductOwner findByProductIdOwnerIdAndOwnerType(Long productId, Long ownerId, String ownerType);

    List<ProductOwner> findAllByOwnerType(String ownerType);
    Page<ProductOwner> findAllByProductIdInAndOwnerIdAndOwnerType(List<Long> productId, Long ownerId, String ownerType, Pageable pageable);
    Page<ProductOwner> findAllByProductIdInAndOwnerIdInAndOwnerType(List<Long> productId, List<Long> ownerId, String ownerType, Pageable pageable);

    @Query(value = "SELECT * FROM tblmproductowner " +
            "WHERE product_id = :productId " +
            "AND owner_id = :destinationId " +
            "AND owner_type = :destinationType",
            nativeQuery = true)
    ProductOwner findByProductIdAndOwnerIdAndOwnerType(@Param("productId") Long productId,
                                                             @Param("destinationId") Long destinationId,
                                                             @Param("destinationType") String destinationType);

    @Query(value = "SELECT t.unused_qty FROM tblmproductowner " +
            "WHERE product_id = :productId " +
            "AND owner_id = :ownerId " +
            "AND owner_type = :ownerType",
            nativeQuery = true)
    Long findByUnUsedQty(@Param("productId") Long productId,
                                                       @Param("ownerId") Long ownerId,
                                                       @Param("ownerType") String ownerType);

}
