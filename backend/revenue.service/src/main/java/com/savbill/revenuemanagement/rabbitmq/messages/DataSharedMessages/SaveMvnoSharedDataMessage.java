package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import lombok.Data;

@Data
public class SaveMvnoSharedDataMessage {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String fullName;
    private String address;
    private String suffix;
    private String description;
    private String email;
    private String phone;
    private String status;
    private String logfile;
    private String mvnoHeader;
    private String mvnoFooter;
    private Boolean isDelete = false;
    private Integer mvnoPaymentDueDays;
    private String clientId;
    private Double ispCommissionPercentage;
}
