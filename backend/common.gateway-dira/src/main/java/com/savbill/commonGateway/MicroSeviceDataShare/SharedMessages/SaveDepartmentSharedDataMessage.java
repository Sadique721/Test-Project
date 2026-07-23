package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveDepartmentSharedDataMessage {

    private Integer id;

    private String name;

    private String status;

    private Boolean isDelete = false;
    private Integer mvnoId;
    List<Integer> planIds=new ArrayList<>();
}
