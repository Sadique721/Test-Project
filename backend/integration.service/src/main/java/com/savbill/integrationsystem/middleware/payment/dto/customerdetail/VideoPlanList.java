package com.savbill.integrationsystem.middleware.payment.dto.customerdetail;

import lombok.Data;

import java.util.List;

@Data
public class VideoPlanList {

    private List<VideoPlanDetail> videoPlanDetails;
}
