package com.savbill.ticketmanagement.core.dto;

import com.savbill.ticketmanagement.core.modules.Plan.domain.CustPlanMappping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustPlanMapppingDto {

    private Integer id;

    private Integer planId;

    private String service;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Double offerPrice;

    private Double taxAmount;
    private LocalDateTime expiryDate;

    private String startDateString;

    private String endDateString;

    private String expirydateString;

    private String custPlanStatus;

    private Long promisetopay_renew_count;
    private Integer graceDays;

    private String promise_to_pay_remarks;

    private String graceDateTime;

    private String promise_to_pay_startdate;;

    private String promise_to_pay_enddate;

    private Integer custId;

    private String remarks;



    public CustPlanMapppingDto(CustPlanMappping custPlanMappping){
        this.id = custPlanMappping.getId();
        this.startDateString = custPlanMappping.getStartDate().toString();
        this.endDateString = custPlanMappping.getEndDate().toString();
        this.expirydateString = custPlanMappping.getExpiryDate().toString();
        this.custPlanStatus = custPlanMappping.getCustPlanStatus();
        this.custId = custPlanMappping.getCustid();
    }
}
