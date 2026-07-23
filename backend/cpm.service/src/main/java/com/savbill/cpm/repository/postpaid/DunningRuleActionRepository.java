package com.savbill.cpm.repository.postpaid;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.postpaid.DunningRuleAction;

@Repository
public interface DunningRuleActionRepository extends JpaRepository<DunningRuleAction, Integer> {

}
