package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "TBLTSNMPCLIENTPROFILE")
@ApiModel(value = "Client Entity",description = "This is Client entity which is used to update client data")
@Data
public class SNMPClientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated Profile Id",required = true)
    @Column(name="snmpclientid", nullable = false)
    private Long snmpClientId;

    @ApiModelProperty(notes = "This is attribute to snpm profile destinationIP")
    @Column (name="destinationip", nullable = false , length = 450)
    private String destinationIp;

    @ApiModelProperty(notes = "This is attribute to snpm profile destinationPORT")
    @Column (name="destinationport", nullable = false , length = 250)
    private String destinationPort;

    @ApiModelProperty(notes = "This is attribute to snpm profile baseOID")
    @Column (name="baseoid", nullable = false , length = 250)
    private String baseOid;

    @ApiModelProperty(notes = "This is attribute to snpm profile newOID")
    @Column (name="newoid", nullable = false , length = 250)
    private String newOid;

    @ApiModelProperty(notes = "This is attribute to snpm profile community String")
    @Column (name="communitystring", nullable = false , length = 250)
    private String communityString;

    @ApiModelProperty(notes = "This is attribute to snpm profile version")
    @Column (name="snmpversion", nullable = false , length = 250)
    private String snmpVersion;

    @ApiModelProperty(notes = "This is attribute to snpm profile base Value")
    @Column (name="basevalue", nullable = false , length = 250)
    private String baseValue;

    @ApiModelProperty(notes = "This is attribute to snpm profile new Value")
    @Column (name="newvalue", nullable = false , length = 250)
    private String newValue;
}
