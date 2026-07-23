package com.savbill.revenuemanagement.productmanagement.Charge.repocitory;

import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer> {
    @Query(value = "select * from tblmcharges WHERE CHARGEID in :chargeIds",nativeQuery = true)
    List<Charge> findByChargeIds(@Param("chargeIds") List<Integer> chargeIds);


    @Query("SELECT r.charge FROM PostpaidPlanCharge r WHERE r.plan.id = :planId")
    Optional<Charge> findChargeByPlanId(@Param("planId") Integer planId);


}
