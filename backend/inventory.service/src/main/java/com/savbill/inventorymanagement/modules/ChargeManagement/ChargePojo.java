package com.savbill.inventorymanagement.modules.ChargeManagement;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.Services.Services;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChargePojo extends Auditable implements IBaseDto {


    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String desc;

    @NotNull
    private String chargetype;

    @NotNull
    private double price;

    private Integer taxId;

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
    private Boolean isinventorycharge;

    @Override
    public String toString() {
        return "ChargePojo [id=" + id +", ledgerId=" + ledgerId  +", name=" + name + ", desc=" + desc + ", chargetype=" + chargetype + ", price="
                + price + ", taxid=" + taxId + ", discountid=" + discountid +", serviceid="+services+"]";
    }

    @Override
    public Long getIdentityKey() {
        return Long.valueOf(id);
    }
}
