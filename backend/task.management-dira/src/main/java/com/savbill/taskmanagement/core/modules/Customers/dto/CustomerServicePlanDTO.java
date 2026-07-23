package com.savbill.taskmanagement.core.modules.Customers.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerServicePlanDTO {

    private List<CustomerPlanDTO> planList;

    private List<CustomerServiceDTO> serviceList;


    private String name;

    private Boolean isAvaileble;

    private Integer custId;

    private Boolean isShowAllService;

}
