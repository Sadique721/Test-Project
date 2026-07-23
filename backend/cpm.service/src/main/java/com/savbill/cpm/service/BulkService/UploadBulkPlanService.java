package com.savbill.cpm.service.BulkService;

import com.savbill.cpm.model.postpaid.PostpaidPlan;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadBulkPlanService {
    public String uploadBulkData(MultipartFile file, Integer mvnoId, String username);

    void updatePlansAndCustmapping(List<PostpaidPlan> updatedPostpaidPlans);
    }
