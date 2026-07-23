package com.savbill.partnermanagement.modules.Tax.repository;
import com.savbill.partnermanagement.modules.Tax.domain.TaxTypeTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxTypeTierRepository extends JpaRepository<TaxTypeTier, Integer> {
}
