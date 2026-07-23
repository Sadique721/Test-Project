package com.savbill.notification.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Data
@Table(name = "TBLSCREENSROLEMAPPING")
public class RoleScreens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated mapping id", name = "rolescreenId", required = true)
    @Column(name = "rolescreenid", nullable = false)
    private Long roleScreenId;

    @ApiModelProperty(notes = "Screen Id")
    @Column(name = "screenid")
    private Long screenId;

    @ApiModelProperty(notes = "Role Id")
    @Column(name = "roleid")
    private Long roleId;

    @ApiModelProperty(notes = "Read Access")
    @Column(name = "readonly", nullable = false)
    private boolean readOnly;

    @ApiModelProperty(notes = "The database mnvoid")
    @Column(name = "mvnoid")
    private Long mvnoId;
    
    @ApiModelProperty(notes = "Create Update Access")
    @Column(name = "createupdateonly", nullable = false)
    private boolean createUpdateOnly;
    
    @ApiModelProperty(notes = "Delete Access")
    @Column(name = "deleteonly", nullable = false)
    private boolean deleteOnly;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "screenid",insertable = false,updatable = false)
    private Screens screens;
}
