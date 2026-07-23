package com.savbill.revenuemanagement.core.dto.customer;

import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
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

    private Integer custServiceMappingId;


    private Integer custId;

    private String remarks;



    public CustPlanMapppingDto(CustPlanMappping custPlanMappping){
        this.id = custPlanMappping.getId();
        this.startDateString = custPlanMappping.getStartDate().toString();
        this.endDateString = custPlanMappping.getEndDate().toString();
        this.expirydateString = custPlanMappping.getExpiryDate().toString();
        this.custPlanStatus = custPlanMappping.getCustPlanStatus();
        this.custServiceMappingId=custPlanMappping.getCustServiceMappingId();
        this.custId = custPlanMappping.getCustomer().getId();
        this.remarks = custPlanMappping.getRemarks();
    }

    public CustPlanMapppingDto(CustPlanMappping custPlanMappping,String inGrace){
            this.id = custPlanMappping.getId();
            this.startDateString = custPlanMappping.getStartDate().toString();
            this.endDateString = custPlanMappping.getEndDate().toString();
            this.expirydateString = custPlanMappping.getExpiryDate().toString();
            this.custPlanStatus = custPlanMappping.getCustPlanStatus();
            this.promisetopay_renew_count = custPlanMappping.getPromisetopay_renew_count();
            this.graceDays = custPlanMappping.getGraceDays();
            this.promise_to_pay_remarks = custPlanMappping.getPromise_to_pay_remarks();
            this.graceDateTime = custPlanMappping.getGraceDateTime().toString();
            this.promise_to_pay_startdate = custPlanMappping.getPromise_to_pay_startdate().toString();
            this.promise_to_pay_enddate = custPlanMappping.getPromise_to_pay_enddate().toString();
            this.custServiceMappingId=custPlanMappping.getCustServiceMappingId();
            this.expiryDate=custPlanMappping.getExpiryDate();
           this.custId = custPlanMappping.getCustomer().getId();
    }
}
