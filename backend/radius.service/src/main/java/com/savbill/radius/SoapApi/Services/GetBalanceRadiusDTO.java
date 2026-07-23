package com.savbill.radius.SoapApi.Services;

import lombok.Data;

@Data
public class GetBalanceRadiusDTO {
    private String subscriberId;
    private String planId;
    private String planName;
    private Long mvnoId;

}
