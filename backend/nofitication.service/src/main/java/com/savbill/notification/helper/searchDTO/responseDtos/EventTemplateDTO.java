package com.savbill.notification.helper.searchDTO.responseDtos;

import com.savbill.notification.entity.Template;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTemplateDTO {
    private Long eventId;
    private String eventName;
    private String eventType;
    private String description;
    private String status;
    private String timeInterval;
    private String timeIntervalType;
    private String emailSubject;
    private String toEmailId;
    private String ccEmailId;
    private String bccEmailId;
    private Long emailConfigId;
    private String constraintType;
    private String columnValue;
    private String regex;
    private String regexGroupIndex;
    private Boolean systemGenerated = false;
    private List<Template> templates;
}

