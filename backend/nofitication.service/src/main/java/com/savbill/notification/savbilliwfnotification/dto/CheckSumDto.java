package com.savbill.notification.savbilliwfnotification.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CheckSumDto {
    private Long id;

    private String checkSumValue;

    private Date createdDate;

    private String filePath;

    private String driverId;

    private boolean isChanged;

    private String currentChecksum;

    private String sourceIps;

    private String instanceId;


}
