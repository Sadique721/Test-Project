package com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage;

import lombok.Data;

@Data
public class UpdateMvnoSharedDataMessage {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String suffix;
    private String description;
    private String email;
    private String phone;
    private String status;
    private String logfile;
    private String mvnoHeader;
    private String mvnoFooter;
    private Boolean isDelete = false;
}
