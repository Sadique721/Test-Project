package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblmunknown_profile_mapping")
@ApiModel(value = "UnknownProfileMapping",description = "This is Unknown Profile Mapping entity.")
@Data
public class UnknownProfileMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "UnknownProfileMapping attribute id",required = true)
    @Column(name = "attributeid", nullable = false)
    private Long attributeId;

    @ApiModelProperty(notes = "This is client group id",required = true)
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is UnknownProfileMapping attribute",required = true)
    @Column(name = "attribute", nullable = false , length = 250)
    private String attribute;

    @ApiModelProperty(notes = "This is UnknownProfileMapping attribute value",required = true)
    @Column(name = "attributevalue", nullable = false , length = 250)
    private String attributeValue;

    @ApiModelProperty(notes = "This is inactiveProfileMapping checkitem", required = false)
    @Column(name = "checkitem", length = 100)
    private String checkitem;
}
