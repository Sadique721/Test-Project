package com.savbill.revenuemanagement.productmanagement.parentchildmapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentChildMappingRepo extends JpaRepository<ParentChildMappingRel , Long> {
    List<ParentChildMappingRel> findAllByChildUsernameAndParentCustomerAndMvno(String username , Long parentCustId , Long mvnoId);
    @Query(value = "SELECT * FROM tblparentchildmappingrel WHERE child_cust_id = :childId AND parent_cust_id = :parentId  AND (is_delete IS NULL OR is_delete = false)", nativeQuery = true)
    Optional<ParentChildMappingRel> findByChildAndParentCustomer(
            @Param("childId") Long childId,
            @Param("parentId") Long parentId
    );
}
