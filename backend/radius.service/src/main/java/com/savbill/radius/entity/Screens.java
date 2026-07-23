package com.savbill.radius.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Data
@Table(name = "TBLSCREENS")
public class Screens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated screen id", name = "screenId", required = true)
    @Column(name = "screenid", nullable = false)
    private Long screenId;

    @ApiModelProperty(notes = "Name of the screen", required = true)
    @Column(name = "screenname", nullable = false, length = 20)
    private String screenname;
    
    @ApiModelProperty(notes = "Display name of the screen", required = true)
    @Column(name = "displayname", nullable = false, length = 20)
    private String dispalyname;

    @ApiModelProperty(hidden = true)
    @Column (name="mvnoid", nullable = false)
    private Integer mvnoId;
}
