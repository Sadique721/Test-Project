package com.diameter.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCustomerServiceMappingImsiMessage {

    private Integer customerId;
    private String msisdn;
    private String imsi;
}
