package com.savbill.cpm.pojo.api;

import com.savbill.cpm.model.postpaid.PostpaidPlan;

import com.savbill.cpm.model.radius.VoucherLinkType;
import lombok.Data;

@Data
public class VoucherMasterPojo {
    private Integer id;
    private String vcName;
    private Integer vcQty;
    private PostpaidPlan plan;
    private Integer plid;
    private String numeric;
    private String uppercase;
    private String lowercase;
    private Integer voucherlength;
    private Integer vouchervalidity;

    private VoucherLinkType linkType;
    private Double voucherAmount;

}
