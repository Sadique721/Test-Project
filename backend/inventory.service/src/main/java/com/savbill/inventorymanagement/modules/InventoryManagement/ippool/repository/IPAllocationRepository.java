package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.repository;

import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain.IPAllocation;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface IPAllocationRepository extends JpaRepository<IPAllocation, Long> {
    public List<IPAllocation> findAllByCustId(Long custId);
}
