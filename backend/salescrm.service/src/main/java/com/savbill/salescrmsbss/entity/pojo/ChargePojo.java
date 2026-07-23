package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.Charge;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChargePojo{


    private Integer id;
    private String name;
    private String chargetype;
    private double price;
    private Integer taxid;
    private double actualprice;
    private Boolean isDelete = false;
    private String chargecategory;
    private Integer mvnoId;
    private Long buId;
    private String saccode;
    private String ledgerId;

    public ChargePojo(Charge charge){
        setId(charge.getApiGatewayChargeId().intValue());
        setName(charge.getName());
        setChargetype(charge.getChargetype());
        setSaccode(charge.getSaccode());
        setLedgerId(charge.getLedgerId());
        setPrice(charge.getPrice());
        setTaxid(charge.getTaxId());
        setActualprice(charge.getActualprice());
        setIsDelete(charge.getIsDelete());
        setChargecategory(charge.getChargecategory());
        setMvnoId(charge.getMvnoId());
        setBuId(charge.getBuId());
    }

}
