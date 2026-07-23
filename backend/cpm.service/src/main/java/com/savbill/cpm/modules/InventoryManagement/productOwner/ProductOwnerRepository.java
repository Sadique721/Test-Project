package com.savbill.cpm.modules.InventoryManagement.productOwner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOwnerRepository extends JpaRepository<ProductOwner, Long>, QuerydslPredicateExecutor<ProductOwner> {
    @Query(value = "select * from tbltproductowner m where m.product_id=:productId and m.owner_id=:ownerId and owner_type =:ownerType",nativeQuery = true)
    ProductOwner findByProductIdOwnerIdAndOwnerType(Long productId, Long ownerId, String ownerType);
}
