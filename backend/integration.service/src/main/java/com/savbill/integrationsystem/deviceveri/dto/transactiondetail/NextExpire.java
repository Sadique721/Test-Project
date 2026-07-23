package com.savbill.integrationsystem.deviceveri.dto.transactiondetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NextExpire {

    @JsonProperty("date")
    private String date;
    
    @JsonProperty("timezone")
    private String timezone;
    
    @JsonProperty("timezone_type")
    private Integer timezoneType;

}
