package com.savbill.revenuemanagement.core.service.prepaid;

import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class UpdateCprMessage {
    List<Map.Entry<Integer, String>> custPackAndEndDatePair;

    public UpdateCprMessage(List<Map.Entry<Integer, String>> custPackAndEndDatePair) {
        this.custPackAndEndDatePair = custPackAndEndDatePair;
    }

    public List<Map.Entry<Integer, String>> getCustPackAndEndDatePair() {
        return custPackAndEndDatePair;
    }

    public void setCustPackAndEndDatePair(List<Map.Entry<Integer, String>> custPackAndEndDatePair) {
        this.custPackAndEndDatePair = custPackAndEndDatePair;
    }
}
