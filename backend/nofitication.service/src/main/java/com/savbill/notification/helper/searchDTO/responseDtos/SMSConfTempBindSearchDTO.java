package com.savbill.notification.helper.searchDTO.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSConfTempBindSearchDTO {
    private Long smsConfigId;
    private String smsUrl;
}
