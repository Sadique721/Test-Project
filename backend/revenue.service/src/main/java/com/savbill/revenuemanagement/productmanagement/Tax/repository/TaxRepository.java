package com.savbill.revenuemanagement.productmanagement.Tax.repository;

import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<Tax,Integer> {
}
