package com.savbill.taskmanagement.core.modules.tasks.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
@Getter
@Setter
public class TicketETRPojo {

    private String templateContent;

    private HashMap<String,Boolean> selectedNotificationType = new HashMap<>();

    private LocalTime notificationTime;

    private LocalDate notificationDate;

    private Integer staffId;

    private Integer taskOwnerStaffId;

    private Integer ticketId;

    private String ticketNumber;

    private String customerMobileNo;

    private String customerEmailId;

    private Integer mvnoId;

    private String  status;

    private String  sender;

    private Boolean  isTemplateDynamic;

    private String remark;


}
