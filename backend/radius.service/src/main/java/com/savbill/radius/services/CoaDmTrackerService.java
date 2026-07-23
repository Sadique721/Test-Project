package com.savbill.radius.services;

import com.savbill.radius.aaa.data.CoaDmTracker;

public interface CoaDmTrackerService {

    public void saveNewCoaDmTracker(CoaDmTracker coaDmTracker);

    public boolean checkCoaDmExistsForPlanExpire(CoaDmTracker coaDmTracker);
    public boolean checkCoaDmExistsForPlanQuotaExhaustOrQuotaReset(CoaDmTracker coaDmTracker);
    public boolean checkCoaDmExistsForTimeBasePolicy(CoaDmTracker coaDmTracker);

}
