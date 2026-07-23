package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
}