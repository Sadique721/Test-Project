package com.savbill.partnermanagement.rabbitmq.product;

import lombok.Data;

@Data
public class SaveDiscountSharedMessage {
    private Integer id;

    private String name;

    private String desc;

    private String status;

    private Integer mvnoId;

//    List<DiscountMapping> discMappingList = new ArrayList<>();
//
//
//    List<DiscountPlanMapping> planMappingList = new ArrayList<>();

    private Boolean isDelete = false;

    private Long buId;
}
