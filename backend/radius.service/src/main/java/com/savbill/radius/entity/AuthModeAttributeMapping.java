package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblmauthmodeattributemapping")
@ApiModel(value = "Coa DM client Group Profile Mapping ",description = "This is coa dm client group mapping entity.")
@Data
public class AuthModeAttributeMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "This is client reply attribute id")
    @Column(name = "id", nullable = false)
    private Long id;

    @ApiModelProperty(notes = "This is radius profile id")
    @Column(name = "radiusprofileid", nullable = false)
    private Long radiusProfileId;

    @ApiModelProperty(notes = "This is authentication mode")
    @Column(name ="authentication_mode")
    private String authenticationMode;

    @ApiModelProperty(notes = "This is column Name for mapping")
    @Column(name ="column_name")
    private String columnName;

}
