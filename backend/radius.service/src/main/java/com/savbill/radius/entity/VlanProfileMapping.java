package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblvlan_profile_mapping")
@ApiModel(value = "VlanProfileMapping",description = "This is Vlan Profile Mapping entity.")
@Data
public class VlanProfileMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "VlanProfileMapping attribute id",required = true)
    @Column(name = "id", nullable = false)
    private Long id;

    @ApiModelProperty(notes = "This is client group id",required = true)
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is VlanProfileMapping attribute",required = true)
    @Column(name = "attribute", nullable = false , length = 250)
    private String attribute;

    @ApiModelProperty(notes = "This is VlanProfileMapping Coloumn value",required = true)
    @Column(name = "coloumn", nullable = false , length = 250)
    private String coloumn;
}
