package com.savbill.cpm.repository.common;

import com.savbill.cpm.model.common.EnterpriseETRAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface EnterpriseETRAuditRepository extends JpaRepository<EnterpriseETRAudit,Integer>, QuerydslPredicateExecutor<EnterpriseETRAudit> {

    List<EnterpriseETRAudit> findByCustId(Long custid);
}
