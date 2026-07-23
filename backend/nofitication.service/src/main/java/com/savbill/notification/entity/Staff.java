package com.savbill.notification.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "TBLMSTAFF")
@Data
public class Staff 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated Staff Id", required = true)
    @Column(name = "staffid", nullable = false)
    private Long staffId;

    @ApiModelProperty(notes = "Name of the user", required = true)
    @Column(name = "username", nullable = false, length = 250)
    private String userName;

    @ApiModelProperty(notes = "This is password of user", required = true)
    @Column(name = "password", nullable = false, length = 250)
    private String password;

    @ApiModelProperty(notes = "The database mnvoid")
    @Column(name = "mvnoid")
    private Long mvnoId;
    
    @ApiModelProperty(notes = "This is role id that bind the role with staff",required=true)
	@OneToOne
	@JoinColumn(name="roleid")
	private Role role;
}
