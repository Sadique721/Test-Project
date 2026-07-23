package com.savbill.radius.entity;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "TBLMROLE")
public class Role {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated role id", name = "roleId", required = true)
    @Column(name = "roleid", nullable = false)
    private Long roleId;

    @ApiModelProperty(notes = "Name of the role (Ex. ADMIN)", required = true)
    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @ApiModelProperty(notes = "This is mvnoid for role")
    @Column(name = "mvnoid", nullable = false, length = 10)
    private Long mvnoId;


    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<RoleACLEntry> roleAclEntry = new ArrayList<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
}
