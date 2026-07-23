package com.savbill.radius.helper;

import lombok.Data;

@Data
public class RequestDto {
    private String fromDate;

    private String toDate;

    private String username;

    private String replymessage;

    private String packettype;

    private String clientip;

    private String clientgroup;



}
