package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ThirdPartyPlanFetchDTO {


    private List<Integer> sa;

    private List<String> planGroupTypes;

    private String planType;


}
