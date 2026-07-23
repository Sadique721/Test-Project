package com.savbill.cpm.modules.CustomerDBR.repository;

import com.savbill.cpm.modules.CustomerDBR.domain.CustomerChargeDBR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerChargeDBRRepository extends JpaRepository<CustomerChargeDBR, Long>, QuerydslPredicateExecutor<CustomerChargeDBR> {

    List<CustomerChargeDBR> findAllByServiceIdIn(List<Long> serviceIds);

    boolean existsByCprid(Long cprid);
}
