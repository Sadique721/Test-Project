package com.savbill.cpm.pojo.api;

import com.savbill.cpm.model.common.Auditable;

import lombok.Data;

@Data
public class ParentPojo extends Auditable {

    Integer errCode;
    String errMessage;

}
