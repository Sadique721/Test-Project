package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StaffUserAllPojo {
    private Integer id;
    @NotNull
    private String username;
    @NotNull
    private String fullName;
    private String mobileNumber;
    private String product;
}
