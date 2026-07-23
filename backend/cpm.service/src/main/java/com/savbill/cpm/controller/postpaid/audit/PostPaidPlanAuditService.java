package com.savbill.cpm.controller.postpaid.audit;

import com.savbill.cpm.model.postpaid.PostpaidPlan;
import com.savbill.cpm.modules.Reseller.mapper.PageableResponse;
import com.savbill.cpm.modules.Voucher.module.PaginationDTO;

public interface PostPaidPlanAuditService {
    PageableResponse getPlanAudit(Integer loggedInMvno, PaginationDTO dto);

    public boolean updatePostpaidPlan(PostpaidPlan existingPlan, PostpaidPlan updatedPlan, Integer staffId, String username);
}
