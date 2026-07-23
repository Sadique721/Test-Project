package com.savbill.integrationsystem.mvno;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MvnoDTO  {

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

    private Long passwordPolicyId;

    private String eventName;

    private Long eventId;


    @Transient
    private String address;
    private String fullName;
    private Boolean isDelete = false;
    private Long roleId;
    private Integer custInvoiceRefId;
    private Boolean mvnoDeactivationFlag;
    private Integer chargeId;
    private byte[] profileImage;
    private String logo_file_name;
    private Integer mvnoPaymentDueDays;
    private Boolean isTwoFactorEnabled;
    private String authEventName;
    private Integer ispBillDay;
    private Double ispCommissionPercentage;
    private String clientId;
    private Long profileId;
}
