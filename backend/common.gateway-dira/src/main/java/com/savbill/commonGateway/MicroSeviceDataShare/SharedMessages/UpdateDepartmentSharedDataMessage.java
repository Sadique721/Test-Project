package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateDepartmentSharedDataMessage {

    private Integer id;

    private String name;

    private String status;

    private Boolean isDelete;
    private Integer mvnoId;
    List<Integer> planIds=new ArrayList<>();
}
