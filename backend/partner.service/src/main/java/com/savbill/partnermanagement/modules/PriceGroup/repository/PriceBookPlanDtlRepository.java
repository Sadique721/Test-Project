package com.savbill.partnermanagement.modules.PriceGroup.repository;

//import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.savbill.partnermanagement.modules.partner.entity.PriceBookPlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceBookPlanDtlRepository extends JpaRepository<PriceBookPlanDetail,Long>, QuerydslPredicateExecutor<PriceBookPlanDetail> {

}
