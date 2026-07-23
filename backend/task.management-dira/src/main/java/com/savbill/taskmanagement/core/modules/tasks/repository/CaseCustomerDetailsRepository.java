package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseCustomerDetailsRepository extends JpaRepository<CaseCustomerDetails,Integer> {
    List<CaseCustomerDetails> findByCaseId(Integer caseId);

    List<CaseCustomerDetails>findByCustomerId(Integer customerId);

    List<CaseCustomerDetails> findAllByCaseIdIn(List<Integer> integerList);
}
