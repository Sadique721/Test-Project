package com.savbill.notification.entity;

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
@Table(name = "TBLMROLE")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated role id", name = "roleId", required = true)
    @Column(name = "roleid", nullable = false)
    private Long roleId;

    @ApiModelProperty(notes = "Name of the role (Ex. ADMIN)", required = true)
    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @ApiModelProperty(notes = "This is mvnoid for role")
    @Column(name = "mvnoid", nullable = false, length = 10)
    private Long mvnoId;
}
