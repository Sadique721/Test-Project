package com.savbill.cpm.modules.DisconnectSubscriber.model;

import lombok.Data;

@Data
public class UserDisconnectByNameReqDTO {
    private Integer userId;
    private String remark;
    private String reqType;
}
