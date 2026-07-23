package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveBusinessUnitSharedDataMessage {


    private Integer id;

    private String buname;

    private String bucode;

    private String status;


    private String planBindingType;


    private Boolean isDeleted ;


    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
//    private List<InvestmentCode> investmentCodeid=new ArrayList<>();
}
