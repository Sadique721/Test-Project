
package com.savbill.integrationsystem.middleware.payment.dto.customerdetail;

import lombok.Data;

@Data
public class PgCustomerDetail {

    private String status;
    private String expiryDate;
    private String returnCode;
    private String returnMessage;
    private String name;
    private String userId;
    private String address;
    private String area;
    private String state;
    private String city;
    private String nation;
    private String mobileNo;
    private String telephone;
    private String eMail;
    private String currentPlanName;
    private String outstandingAmount;
    private String onuId;
    private String partnerName;
    private VideoPlanList videoPlanList;

}
