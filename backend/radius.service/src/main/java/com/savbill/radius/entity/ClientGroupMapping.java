package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "TBLMCLIENTGROUPMAPPING")
@ApiModel(value = "ClientGroupMapping",description = "This is Client Group Mapping entity.")
@Data
public class ClientGroupMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "Client group entry id",required = true)
    @Column(name = "clientgroupentryid", nullable = false)
    private Long clientGroupEntryId;

    @ApiModelProperty(notes = "This is client id",required = true)
    @Column(name = "clientid", nullable = false)
    private Long clientId;

    @ApiModelProperty(notes = "This is check expression",required = true)
    @Column(name = "checkitem", nullable = false , length = 250)
    private String checkItem;

    @ApiModelProperty(notes = "This is client group id",required = true)
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is priority",required = true)
    @Column(name = "priority", nullable = false , length = 250)
    private Integer priority;

    @Transient
    ClientGroup clientGroupData;

    @Transient
    ClientReply clientReplyData;

}
