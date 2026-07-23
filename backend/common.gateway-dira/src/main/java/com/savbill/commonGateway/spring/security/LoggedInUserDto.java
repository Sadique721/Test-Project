package com.savbill.commonGateway.spring.security;


import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class LoggedInUserDto {

    private String userName;
    private String password;

    private String roleId ;

    private List<Long> roleList;

    private String firstName;

    private String lastName;

    private LocalDateTime lastLoginTime;

    private Integer staffId;

    private Integer partnerId;

    private ServiceArea serviceAreaId;

    private Integer mvnoId;

    private List<Integer> serviceAreaIdList=new ArrayList<>();

    private List<Long> buIds;

    private Boolean isPasswordExpired = false;

    public LoggedInUserDto(String password, String firstName, String lastName, LocalDateTime lastLoginTime, Integer staffId, Integer partnerId, Integer mvnoId, Boolean isPasswordExpired, String username) {
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastLoginTime = lastLoginTime;
        this.staffId = staffId;
        this.partnerId = partnerId;
        this.mvnoId = mvnoId;
        this.isPasswordExpired = isPasswordExpired;
        this.userName = username;
    }
}
