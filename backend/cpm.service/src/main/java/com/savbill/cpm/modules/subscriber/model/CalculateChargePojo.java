package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

@Data
public class CalculateChargePojo {
    Integer custChargeId;
    Double fullAmount;
    Double proratedAmount;
}
