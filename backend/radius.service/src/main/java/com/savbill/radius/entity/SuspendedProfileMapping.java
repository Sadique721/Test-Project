package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblmsuspendedprofilemapping")
@ApiModel(value = "SuspendedProfileMapping",description = "This is Suspended Profile Mapping entity.")
@Data
public class SuspendedProfileMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "SuspendedProfileMapping attribute id",required = true)
    @Column(name = "attributeid", nullable = false)
    private Long attributeId;

    @ApiModelProperty(notes = "This is client group id",required = true)
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is SuspendedProfileMapping attribute",required = true)
    @Column(name = "attribute", nullable = false , length = 250)
    private String attribute;

    @ApiModelProperty(notes = "This is SuspendedProfileMapping attribute value",required = true)
    @Column(name = "attributevalue", nullable = false , length = 250)
    private String attributeValue;

    @ApiModelProperty(notes = "This is SuspendedProfileMapping checkItem", required = false)
    @Column(name = "checkitem", length = 100)
    private String checkitem;
}
