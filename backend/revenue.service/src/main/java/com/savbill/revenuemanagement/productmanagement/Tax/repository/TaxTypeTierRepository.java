package com.savbill.revenuemanagement.productmanagement.Tax.repository;

import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxTypeTierRepository extends JpaRepository<TaxTypeTier, Integer> {
}
