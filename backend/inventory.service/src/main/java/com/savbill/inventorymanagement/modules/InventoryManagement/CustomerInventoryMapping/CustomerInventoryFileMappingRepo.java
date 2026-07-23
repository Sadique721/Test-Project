package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInventoryFileMappingRepo extends JpaRepository<CustomerInventoryFileMapping , Long> {

    @Query(value = "SELECT * FROM tbltcustomer_inventory_file_mapping WHERE customer_inventory_mapping_id = :customerInventoryMappingId",
            nativeQuery = true)
    List<CustomerInventoryFileMapping> findByCustomerInventoryMappingId(@Param("customerInventoryMappingId") Long customerInventoryMappingId);

    @Query(value = "SELECT * FROM tbltcustomer_inventory_file_mapping WHERE uniquename = :uniquename",
            nativeQuery = true)
    CustomerInventoryFileMapping findByCustomerInventoryByUniqueName(@Param("uniquename") String uniquename);

    List<CustomerInventoryFileMapping> findByCustomerInventoryMappingAndSection(Long customerInventoryMappingId, String section);


}
