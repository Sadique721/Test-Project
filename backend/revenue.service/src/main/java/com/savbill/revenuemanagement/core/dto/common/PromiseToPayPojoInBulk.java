package com.savbill.revenuemanagement.core.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class PromiseToPayPojoInBulk {

    private List<PromiseToPayPojo> promiseToPay;
    String promise_to_pay_remarks;
    Integer graceDays;
    Integer custId;
}
