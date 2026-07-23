package com.savbill.radius.helper;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class DeviceDriverDTO {


    @ApiModelProperty(notes = "The database generated device id")
    private Long deviceDriverId;

    @ApiModelProperty(notes = "This is device driver username")
    private String name;

    @ApiModelProperty(notes = "This is device driver address")
    private String address;

    @ApiModelProperty(notes = "This is device driver administartor name")
    private String userName;

    @ApiModelProperty(notes = "This is device driver administartor password")
    private String password;

    @ApiModelProperty(notes = "This is LDAP connection timeout")
    private Integer connectionTimeout;

    @ApiModelProperty(notes = "This is LDAP connection status duration")
    private Integer statusDuration;

    @ApiModelProperty(notes = "This is LDAP minimum connection pool")
    private Integer minimumConnectionPool;

    @ApiModelProperty(notes = "This is LDAP maximum connection pool")
    private Integer maximumConnectionPool;

    @ApiModelProperty(notes = "This is LDAP user DN", required = true)
    private String userDn;

    @ApiModelProperty(notes = "This is LDAP size limit", required = true)
    private Integer sizeLimit;

    @ApiModelProperty(notes = "This is LDAP version", required = true)
    private Double version;

    @ApiModelProperty(notes = "This is Username Attribute", required = true)
    private String  userNameAttribute;

    @ApiModelProperty(notes = "This is Password Attribute", required = true)
    private String  passwordAttribute;
}
