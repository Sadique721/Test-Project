package com.savbill.notification.helper.searchDTO.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSReceiveSearchDTO {
    private Integer staffId;
    private String fullName;
    private String mobileNumber;
}
