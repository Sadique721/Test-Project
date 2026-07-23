package com.savbill.cpm.pojo.api;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WithdrawCommissionDto {
    private Integer partnerId;
    private String paymentMode;
    private Double withdrawAmount;
    private LocalDate paymentDate;
    private String referNo;
    private String remark;
    private  String bank;
    private String branch;
}
