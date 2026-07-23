package com.savbill.notification.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchSmsRespDto {
    private Long smsConfigId;
    private String smsUrl;
    private Long mvnoId;
    private Long buId;
    private Boolean configStatus;
    private String serviceType;
}