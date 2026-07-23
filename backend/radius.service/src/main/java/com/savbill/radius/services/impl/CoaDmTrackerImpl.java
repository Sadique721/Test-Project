package com.savbill.radius.services.impl;

import com.savbill.radius.aaa.data.CoaDmTracker;
import com.savbill.radius.repository.CoaDmTrackerRepository;
import com.savbill.radius.services.CoaDmTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoaDmTrackerImpl implements CoaDmTrackerService {

    @Autowired
    private CoaDmTrackerRepository coaDmTrackerRepository;

    @Override
    public void saveNewCoaDmTracker(CoaDmTracker coaDmTracker) {
        coaDmTrackerRepository.save(coaDmTracker);
    }

    @Override
    public boolean checkCoaDmExistsForPlanExpire(CoaDmTracker coaDmTracker) {
        if(coaDmTracker.getStrAcctSessionId() != null) {
            return coaDmTrackerRepository.existsByCauseAndCustpackageidAndStrAcctSessionId(coaDmTracker.getCause(), coaDmTracker.getCustpackageid(), coaDmTracker.getStrAcctSessionId());
        }
        return coaDmTrackerRepository.existsByCauseAndCustpackageid(coaDmTracker.getCause(), coaDmTracker.getCustpackageid());
    }

    @Override
    public boolean checkCoaDmExistsForPlanQuotaExhaustOrQuotaReset(CoaDmTracker coaDmTracker) {
        if(coaDmTracker.getStrAcctSessionId() != null) {
            return coaDmTrackerRepository.existsByCauseAndCustpackageidAndStrAcctSessionId(coaDmTracker.getCause(), coaDmTracker.getCustpackageid(), coaDmTracker.getStrAcctSessionId());
        }
        return coaDmTrackerRepository.existsByCauseAndCustpackageid(coaDmTracker.getCause(), coaDmTracker.getCustpackageid());
    }

    @Override
    public boolean checkCoaDmExistsForTimeBasePolicy(CoaDmTracker coaDmTracker) {
        if(coaDmTracker.getStrAcctSessionId() != null) {
            return coaDmTrackerRepository.existsByCauseAndCustpackageidAndTimeBasePolicyIdAndStrAcctSessionId(coaDmTracker.getCause(), coaDmTracker.getCustpackageid(), coaDmTracker.getTimeBasePolicyId(), coaDmTracker.getStrAcctSessionId());
        }
        return coaDmTrackerRepository.existsByCauseAndCustpackageidAndTimeBasePolicyId(coaDmTracker.getCause(), coaDmTracker.getCustpackageid(), coaDmTracker.getTimeBasePolicyId());
    }
}
