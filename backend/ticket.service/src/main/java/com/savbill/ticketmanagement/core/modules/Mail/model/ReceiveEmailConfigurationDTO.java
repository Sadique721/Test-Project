package com.savbill.ticketmanagement.core.modules.Mail.model;

import lombok.Data;

@Data
public class ReceiveEmailConfigurationDTO {

    private Long id;

    private Boolean isDelete;

    private String name;

    private String userName;

    private String password;

    private String host;

    private String port;

    private Boolean isEnable;

    private Long mvnoId;

    private Long buId;
}
