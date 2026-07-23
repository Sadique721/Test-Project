package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.savbill.cpm.modules.CommonList.model.CommonListDTO;

@Data
public class StatusModel {
    private Integer custId;
    private CommonListDTO currentStatus;
    private List<CommonListDTO> changedStatus;
}
