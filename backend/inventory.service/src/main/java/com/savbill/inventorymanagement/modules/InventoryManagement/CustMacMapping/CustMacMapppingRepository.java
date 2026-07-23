package com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface CustMacMapppingRepository extends JpaRepository<CustMacMappping, Integer>, QuerydslPredicateExecutor<CustMacMappping> {

    List<CustMacMappping> findAllByCustomerIdAndIsDeletedIsFalse(Integer custId);

    List<CustMacMappping> findByCustomerIdAndIsDeletedIsFalse(Integer custId);
    @Transactional
    void deleteByCustomerId(Integer custId);

    CustMacMappping findByMacAddressAndIsDeletedIsFalseAndMacAddressIsNotNull(String maccAddress);
}
