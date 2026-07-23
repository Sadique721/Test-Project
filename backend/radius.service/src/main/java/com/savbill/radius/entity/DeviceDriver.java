package com.savbill.radius.entity;

import com.savbill.radius.helper.DeviceDriverDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMDEVICEDRIVER")
@ApiModel(value = "Device Driver Entity", description = "This is device driver entity which is used to update device driver data")
public class DeviceDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated device id")
    @Column(name = "devicedriverid", nullable = false)
    private Long deviceDriverId;

    @ApiModelProperty(notes = "This is device driver username", required = true)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ApiModelProperty(notes = "This is device driver address", required = true)
    @Column(name = "address", nullable = false)
    private String address;

    @ApiModelProperty(notes = "This is device driver administartor name", required = true)
    @Column(name = "username", nullable = false)
    private String userName;

    @ApiModelProperty(notes = "This is device driver administartor password", required = true)
    @Column(name = "password", nullable = false)
    private String password;


    @ApiModelProperty(notes = "This is LDAP user DN", required = true)
    @Column(name = "user_dn")
    private String userDn;

    @ApiModelProperty(notes = "This is mvno id")
    @Column (name="mvnoid", nullable = false)
    private Integer mvnoId;

    @ApiModelProperty(notes = "This is soft delete column")
    @Column (name="is_delete", nullable = false)
    private Boolean isDelete;

    @CreationTimestamp
    @Column(name = "createdate", nullable=false, updatable = false)
    @JsonProperty("createDate")
    private LocalDateTime createdOn;

    @CreatedBy
    @Column(name="createdby", nullable = false, length = 40,updatable = false)
    private String createdBy;

    @ApiModelProperty(notes = "This is device driver username attribute", required = true)
    @Column(name = "username_attr")
    private String userNameAttribute;

    @ApiModelProperty(notes = "This is device driver password attribute administartor", required = true)
    @Column(name = "password_attr")
    private String passwordAttribute;



    public DeviceDriver (DeviceDriverDTO deviceDriverDTO){
        this.deviceDriverId=deviceDriverDTO.getDeviceDriverId();
        this.userName = deviceDriverDTO.getUserName();
        this.address = deviceDriverDTO.getAddress();
        this.name = deviceDriverDTO.getName();
        this.password = deviceDriverDTO.getPassword();
        if(deviceDriverDTO.getUserDn() != null){
            this.userDn = deviceDriverDTO.getUserDn();
        }
        if(deviceDriverDTO.getUserNameAttribute() != null){
            this.userNameAttribute = deviceDriverDTO.getUserNameAttribute();
        }
        if(deviceDriverDTO.getPasswordAttribute() != null){
            this.passwordAttribute = deviceDriverDTO.getPasswordAttribute();
        }
        this.isDelete = false;
    }







}
