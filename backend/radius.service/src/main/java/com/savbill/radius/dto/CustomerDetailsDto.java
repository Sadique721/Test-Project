package com.savbill.radius.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsDto {

    private Integer responseCode;
    private String responseMessage;
    private List<PlanDetails> planDetails;  // List of Plans
    private String macAddress;
    private String framedIp;
    private String Name;
    private String username;
    private String cafno;
    private String custtype; //Postpaid,Prepaid
    private Integer additionalPolicy;
    private LocalDateTime firstActivationDate;
    private String mobile;
    private String fax;
    private String email;
    private String custcategory;
    private String expirydate;
    private String contactperson;
    private String CUI;
    private String status;
    private String calendarType;
    private LocalDate nextBillDate;
    private String address;
    private String panNo;
    private LocalDate nextQuotaResetDate;

    private String geoLocation;
    private String PARAM1;
    private String PARAM2;
    private String PARAM3;
    private String PARAM4;
    private String PARAM6;
    private String primaryDNS;
    private String secondaryDNS;
    private String primaryIPv6DNS;
    private String GROUPNAME;
    private String CUSTOMERREPLYITEM;
    private String secondaryIPv6DNS;
    private Boolean mac_auth_enable;
    private Boolean mac_provision;
    private Integer concurrentPolicy;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanDetails {
        private String planName;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;
        private String purchaseType;
        private List<QuotaDetails> quotaDetails;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotaDetails {
        private Double totalQuota;
        private Double usedQuota;
        private Double currentSessionUsageVolume;
    }


}
