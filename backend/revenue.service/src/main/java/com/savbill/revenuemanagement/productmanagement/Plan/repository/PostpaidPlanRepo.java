package com.savbill.revenuemanagement.productmanagement.Plan.repository;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.QosPojo;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostpaidPlanRepo extends JpaRepository<PostpaidPlan, Integer> {
    @Query("SELECT plan.name FROM PostpaidPlan plan where plan.id = :id")
    String findNameById(@Param("id") Integer id);

    @Query("SELECT plan.validity FROM PostpaidPlan plan where plan.id = :id")
    Integer findValidityById(@Param("id") Integer id);

    @Query("SELECT new com.savbill.revenuemanagement.core.controller.invoice.postpaid.QosPojo(pp.qospolicy, pp.qospolicy_name) " +
            "FROM PostpaidPlan pp  " +
            "WHERE pp.id IN :planids and pp.qospolicy IS NOT NULL AND pp.qospolicy_name IS NOT NULL ")
    QosPojo findQosDetails(@Param("planids") Integer planids);

    @Query("SELECT  pp.qospolicy_name " +
            "FROM PostpaidPlan pp  " +
            "WHERE pp.id=:planid and pp.qospolicy IS NOT NULL AND pp.qospolicy_name IS NOT NULL ")
    String findQosName(@Param("planid") Integer planid);

    @Query("select new PostpaidPlan(p.id,p.name,p.category,p.newOfferPrice) from PostpaidPlan p where p.id=:id")
    PostpaidPlan getLightPostpaidDTO(Integer id);

    @Query("SELECT p FROM PostpaidPlan p WHERE p.id = :id")
    PostpaidPlan findPostpaidPlanById(@Param("id") Integer id);

    @Query("SELECT new PostpaidPlan(p.id, p.name, p.category, p.offerprice, p.validity, p.unitsOfValidity) " +
            "FROM PostpaidPlan p WHERE p.id = :id")
    Optional<PostpaidPlan> findProjectedById(@Param("id") Integer id);
}
