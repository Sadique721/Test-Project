package com.savbill.partnermanagement.modules.Charge.repocitory;

import com.savbill.partnermanagement.modules.Charge.domain.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer> {
    @Query(value = "select * from tblmcharges WHERE CHARGEID in :chargeIds",nativeQuery = true)
    List<Charge> findByChargeIds(@Param("chargeIds") List<Integer> chargeIds);
}
