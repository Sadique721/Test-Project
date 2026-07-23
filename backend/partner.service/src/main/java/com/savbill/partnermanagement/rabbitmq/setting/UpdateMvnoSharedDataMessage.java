package com.savbill.partnermanagement.rabbitmq.setting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateMvnoSharedDataMessage {
    private Integer id;
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
    private Integer createdById;
    private Integer lastModifiedById;
}
