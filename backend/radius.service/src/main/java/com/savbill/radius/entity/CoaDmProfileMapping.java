package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblmcoadmprofilemapping")
@ApiModel(value = "Coa DM client Group Profile Mapping ",description = "This is coa dm client group mapping entity.")
@Data
public class CoaDmProfileMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "This is client reply attribute id")
    @Column(name = "coadm_clientgroup_mapping_id", nullable = false)
    private Long coadmClienGroupMappingId;

    @ApiModelProperty(notes = "This is client group id")
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is check item")
    @Column(name = "check_item")
    private String checkItem;

    @ApiModelProperty(notes = "This is COA profile id")
    @Column(name = "coa_profile_id")
    private Long coaProfileId;

    @ApiModelProperty(notes = "This is DM profile id")
    @Column(name = "dm_profile_id")
    private Long dmProfileId;

    @ApiModelProperty(notes = "this is transient variable that handle coa/dm or both or none")
    @Column(name = "coadmselection")
    private String coaDmSelection;

    @Column(name = "priority")
    private int priority;
}
