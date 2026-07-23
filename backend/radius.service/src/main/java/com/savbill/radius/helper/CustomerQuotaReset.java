package com.savbill.radius.helper;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerQuotaReset {

    private Integer custId; //custid
    private LocalDate nextBillDate; //NEXTBILLDATE
    private LocalDate nextQuotaResetDate; //NEXTBILLDATE
    private String custType; //customertype
    private String username; //username
    private Integer mvnoId; //MVNOID
    private Long cdrId; //CDRID
    private Integer billDay;//BILLDAY
    private List<CustomerPlanDataForResetQuota> customerPlanData;
}

