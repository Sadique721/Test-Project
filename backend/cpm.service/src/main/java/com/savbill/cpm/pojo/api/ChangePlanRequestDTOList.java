package com.savbill.cpm.pojo.api;

import com.savbill.cpm.modules.subscriber.model.ChangePlanRequestDTO;
import com.savbill.cpm.modules.subscriber.model.DateOverrideDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangePlanRequestDTOList {
    private List<ChangePlanRequestDTO> changePlanRequestDTOList;
    private List<CustChargeOverrideDTO> custChargeDetailsList;
    private RecordPaymentPojo recordPayment;
    private DateOverrideDto dateOverrideDtos;
    private Boolean isTriggerCoaDm;
    private Boolean skipQuotaUpdate;
    private Boolean renewalForBooster;
    private Integer childId;
}
