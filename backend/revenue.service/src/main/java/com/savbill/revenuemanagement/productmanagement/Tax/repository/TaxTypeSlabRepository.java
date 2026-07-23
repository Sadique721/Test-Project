package com.savbill.revenuemanagement.productmanagement.Tax.repository;

import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxTypeSlabRepository extends JpaRepository<TaxTypeSlab, Integer> {
}
