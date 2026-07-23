package com.savbill.cpm.modules.CustomerFeedback.repository;


import com.savbill.cpm.modules.CustomerFeedback.domain.CustomerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback , Long> , QuerydslPredicateExecutor<CustomerFeedback> {

    List<CustomerFeedback> findByCustIdAndIsDeleteFalse(Long custId);

    List<CustomerFeedback> findCustomerFeedbackByCustIdOrderByCreateDateDesc(Long custId);

    List<CustomerFeedback> findCustomerFeedbackByCustIdAndEventOrderByCreateDateDesc(Long custId,String event);
}
