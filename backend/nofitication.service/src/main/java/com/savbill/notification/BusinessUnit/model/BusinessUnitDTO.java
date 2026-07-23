package com.savbill.notification.BusinessUnit.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BusinessUnitDTO {

    private Long id;
    private String buname;
    private String bucode;
    private String status;
    private Boolean isDeleted = false;
    private String planBindingType;
    private Integer mvnoId;

    private List<Long> investmentCodeid;

    private List<String> icnames=new ArrayList<>();

    private Long displayId;
    private String displayName;


}
