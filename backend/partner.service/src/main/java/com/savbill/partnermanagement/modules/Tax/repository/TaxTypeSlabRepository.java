package com.savbill.partnermanagement.modules.Tax.repository;


import com.savbill.partnermanagement.modules.Tax.domain.TaxTypeSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxTypeSlabRepository extends JpaRepository<TaxTypeSlab, Integer> {
}
