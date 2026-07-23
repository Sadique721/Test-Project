package com.savbill.revenuemanagement.core.repository.partner;



import com.savbill.revenuemanagement.core.entity.partner.PostpaidPlanCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostpaidPlanChargeRepo extends JpaRepository<PostpaidPlanCharge, Integer> {
    @Query(value = "select CHARGEID from TBLMPOSTPAIDPLANCHARGEREL t where t.POSTPAIDPLANID =:planId", nativeQuery = true)
    List<Integer> getChargeListByPlanId(@Param("planId") Integer planId);

    @Query(value = "select chargeprice from TBLMPOSTPAIDPLANCHARGEREL t where t.POSTPAIDPLANID =:planId AND CHARGEID=:chargeId", nativeQuery = true)
    List<Double> getChargeListByPlanIdAndChargeId(@Param("planId") Integer planId,@Param("chargeId") Integer chargeId);

    @Query(value = "select CHARGEID from TBLMPOSTPAIDPLANCHARGEREL t where t.POSTPAIDPLANID IN (:planIds)", nativeQuery = true)
    List<Integer> getChargeListByPlanIdList(@Param("planIds") List<Integer> planIds);
}
