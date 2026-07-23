package com.savbill.partnermanagement.modules.Charge.dto;


import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.modules.Services.Services;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChargePojo extends Auditable {


    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String desc;

    @NotNull
    private String chargetype;

    @NotNull
    private double price;

    private Integer taxid;

    private String taxName;

    private Integer discountid;

    private double dbr;

    private double actualprice;

    private Boolean isDelete = false;
    
    private String chargecategory;

    private String saccode;

    private Double taxamount;
    
    private Integer mvnoId;

    private Long buId;
//    private String service;
    private String status;

    private String ledgerId;

    private Boolean royalty_payable;

    //private Boolean ismapping;

    private List<Long> serviceid;

    private List<Integer> servicesid=new ArrayList<>();

    private List<String> serviceNameList = new ArrayList<>();
    private Services services;

    private Integer displayId;
    private String displayName;

    private String businessType;

    private String pushableLedgerId;

    @Override
    public String toString() {
        return "ChargePojo [id=" + id +", ledgerId=" + ledgerId  +", name=" + name + ", desc=" + desc + ", chargetype=" + chargetype + ", price="
                + price + ", taxid=" + taxid + ", discountid=" + discountid +", serviceid="+services+"]";
    }
}
