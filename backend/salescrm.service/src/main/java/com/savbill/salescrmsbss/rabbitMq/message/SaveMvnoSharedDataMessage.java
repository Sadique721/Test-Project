package com.savbill.salescrmsbss.rabbitMq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveMvnoSharedDataMessage {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String address;
    private String fullName;
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
    private String logo_file_name;
    private Integer mvnoPaymentDueDays;
    private Integer ispBillDay;
    private Double ispCommissionPercentage;
}
