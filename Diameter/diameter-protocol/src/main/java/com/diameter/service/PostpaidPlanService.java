package com.diameter.service;

import com.diameter.kafka.SavePlanSharedDataMessage;
import com.diameter.kafka.UpdatePlanSharedDataMessage;

public interface PostpaidPlanService {

    void savePostpaidPlan(SavePlanSharedDataMessage dataMessage) throws Exception;

    void updatePostpaidPlan(UpdatePlanSharedDataMessage dataMessage) throws Exception;

}
