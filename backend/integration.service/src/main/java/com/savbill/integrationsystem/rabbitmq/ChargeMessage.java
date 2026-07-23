package com.savbill.integrationsystem.rabbitmq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeMessage {
    private Integer id;

    private String name;

    private String desc;

    private String chargetype;

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
    private String status;

    private String ledgerId;

    private Boolean royalty_payable;

    private String pushableLedgerId;

    private String Status;

}
