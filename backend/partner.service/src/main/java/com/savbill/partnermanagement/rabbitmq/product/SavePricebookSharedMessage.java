package com.savbill.partnermanagement.rabbitmq.product;


import com.savbill.partnermanagement.modules.partner.entity.PriceBookPlanDetail;
import com.savbill.partnermanagement.modules.partner.entity.PriceBookSlabDetails;
import com.savbill.partnermanagement.modules.partner.entity.ServiceCommission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePricebookSharedMessage {


    private Long id;
    private String bookname;
    private String validfrom;
    private String validto;
    private String status;
    private String description;
    private String commission_on;


    private Boolean isAllPlanSelected=false;

    private Boolean isAllPlanGroupSelected = false;


    private Integer revenueSharePercentage;

    private List<PriceBookPlanDetail> priceBookPlanDetailList = new ArrayList<>();

    private List<ServiceCommission> serviceCommissionList = new ArrayList<>();

    private Boolean isDeleted = false;

    private Integer mvnoId;


    private String agrPercentage;

    private String tdsPercentage;

    private Long buId;

    private List<PriceBookSlabDetails> priceBookSlabDetailsList = new ArrayList<>();

    private String revenueType;
    private Integer createdBYId;
    private String lastModifiedByname;
}
