package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

import java.util.List;

@Data
public class CustPlanMappingStatusMessage {
    private List<CustPlanMappingMessage> custPlanMappings;
}
