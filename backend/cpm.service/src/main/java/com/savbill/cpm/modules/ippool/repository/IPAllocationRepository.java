package com.savbill.cpm.modules.ippool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.ippool.domain.IPAllocation;

import java.util.List;

public interface IPAllocationRepository extends JpaRepository<IPAllocation, Long> {
    public List<IPAllocation> findAllByCustId(Long custId);
}
