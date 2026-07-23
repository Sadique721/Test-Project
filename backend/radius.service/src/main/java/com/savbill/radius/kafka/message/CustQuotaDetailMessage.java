package com.savbill.radius.kafka.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustQuotaDetailMessage {

    private  Integer custQuotaDetailId;
    private  Double currentSessionUsageVolume;
    private  Double currentSessionUsageTime;
    private  Double usedQuota;
}
