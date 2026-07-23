package com.savbill.cpm.service.BulkService;

import com.savbill.cpm.model.postpaid.PostpaidPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsyncService {


    @Autowired
    private UploadBulkPlanService uploadBulkManagementService;


    @Async
    public void doAsync(List<PostpaidPlan> list){
        uploadBulkManagementService.updatePlansAndCustmapping(list);
    }
}