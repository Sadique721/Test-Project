package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage;



import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountMapping;
import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountPlanMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class SaveDiscountSharedMessage {
    private Integer id;

    private String name;

    private String desc;

    private String status;

    private Integer mvnoId;

    List<DiscountMapping> discMappingList = new ArrayList<>();


    List<DiscountPlanMapping> planMappingList = new ArrayList<>();

    private Boolean isDelete = false;

    private Long buId;
}
