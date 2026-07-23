package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.savbill.cpm.modules.qosPolicy.model.QOSPolicyDTO;

@Data
public class QosPolicyDetailsModel {

    private List<CustomerPlansModel> planList;
    private List<QOSPolicyDTO> qosPolicyList;

}
