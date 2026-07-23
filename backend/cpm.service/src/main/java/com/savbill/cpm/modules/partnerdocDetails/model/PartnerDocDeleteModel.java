package com.savbill.cpm.modules.partnerdocDetails.model;

import lombok.Data;

import java.util.List;

@Data
public class PartnerDocDeleteModel {
    private List<Long> docIdList;
    private Integer partnerId;
}
