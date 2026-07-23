package com.savbill.inventorymanagement.modules.CustomerPackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerPackageRepository extends JpaRepository<CustomerPackage, Long>, QuerydslPredicateExecutor<CustomerPackage> {
    public List<CustomerPackage> findAllByCustomersId(Integer id);
    CustomerPackage findByCustPackageId(Integer id);
}
