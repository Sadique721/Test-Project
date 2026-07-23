package com.savbill.notification.helper.searchDTO.responseDtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTempBindSearchDTO {
    private Long eventId;
    private String eventName;
    private String eventType;
    private String description;
    private String status;
    private Timestamp createDate;
    private Timestamp lastModificationDate;
    private Long mvnoId;
    private String emailSubject;
    private String toEmailId;
    private String ccEmailId;
    private String bccEmailId;
    private Long emailConfigId;
    private String constraintType;
    private String serviceType;
    private Boolean systemGenerated;
    private String templateFilePath;
    private String contentType;
    private Boolean isEmailTemplate;
    private Boolean isSMSTemplate;
    private String templateName;
    private String emailTemplateData;
    private String smsTemplateData;
    private String fileName;
    private String content;
    private Boolean isAppendRequired;
    private String appendURL;
    private List<SMSConfTempBindSearchDTO> smsConfTempBindSearchDTOS;
    private List<SMSReceiveSearchDTO> smsReceiveSearchDTOS;
}
