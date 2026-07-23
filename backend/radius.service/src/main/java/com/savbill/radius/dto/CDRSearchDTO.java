package com.savbill.radius.dto;

import lombok.Data;

@Data
public class CDRSearchDTO extends PaginationDTO{

    private String userName;
    private String framedIpAddress;
    private String nasIpAddress;
    private String classAttribute;
    private String acctStatusType;
    private String nasIdentifier;
    private String framedRoute;
    private String nasPortType;
    private String nasPortId;
    private String acctMultiSessionId;
    private String framedIpv6Address;
    private String acctSessionId;

    private Integer custId;
}
