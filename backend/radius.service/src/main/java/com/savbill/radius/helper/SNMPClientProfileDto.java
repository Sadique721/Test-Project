package com.savbill.radius.helper;

import io.swagger.annotations.ApiModelProperty;

public class SNMPClientProfileDto {

    @ApiModelProperty(notes = "This is Primary Id of snpm profile")
    private Long id;
    @ApiModelProperty(notes = "This is attribute to snpm profile destinationIP")
    private String destinationIp;
    @ApiModelProperty(notes = "This is attribute to snpm profile destinationPORT")
    private String destinationPort;
    @ApiModelProperty(notes = "This is attribute to snpm profile baseOID")
    private String baseOid;
    @ApiModelProperty(notes = "This is attribute to snpm profile newOID")
    private String newOid;
    @ApiModelProperty(notes = "This is attribute to snpm profile community String")
    private String communityString;
    @ApiModelProperty(notes = "This is attribute to snpm profile version")
    private String snmpVersion;
    @ApiModelProperty(notes = "This is attribute to snpm profile base Value")
    private String baseValue;
    @ApiModelProperty(notes = "This is attribute to snpm profile new Value")
    private String newValue;
}
