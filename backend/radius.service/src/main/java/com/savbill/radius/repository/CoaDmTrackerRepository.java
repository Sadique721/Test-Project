package com.savbill.radius.repository;

import com.savbill.radius.aaa.data.CoaDmTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoaDmTrackerRepository extends JpaRepository<CoaDmTracker, Long> {

    boolean existsByCauseAndCustpackageid(String cause, Integer cprId);
    boolean existsByCauseAndCustpackageidAndTimeBasePolicyId(String cause, Integer cprId, String timeBasePolicyId);

    boolean existsByCauseAndCustpackageidAndStrAcctSessionId(String cause, Integer cprId, String strAcctSessionId);

    boolean existsByCauseAndCustpackageidAndTimeBasePolicyIdAndStrAcctSessionId(String cause, Integer cprId, String timeBasePolicyId, String strAcctSessionId);
}
