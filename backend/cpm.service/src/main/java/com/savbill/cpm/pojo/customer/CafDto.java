package com.savbill.cpm.pojo.customer;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.service.common.CustomersService;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CafDto {

    Customers customers;
    CustomersService customersService;
    GenericDataDTO genericDataDTO;
    Map<String,Object> map = new HashMap<>();

}
