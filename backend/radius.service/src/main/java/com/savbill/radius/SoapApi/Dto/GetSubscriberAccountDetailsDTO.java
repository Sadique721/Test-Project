package com.savbill.radius.SoapApi.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSubscriberAccountDetailsDTO {
    private String custName;

    private String cStatus;

    private String password;

    private String locationLock;

    private String planId;

    private String creationDate;

}
