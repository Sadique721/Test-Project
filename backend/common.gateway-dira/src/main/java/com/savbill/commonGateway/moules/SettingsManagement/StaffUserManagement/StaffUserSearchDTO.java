package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffUserSearchDTO {

    private Integer id;

    private String firstname;

    private String lastname;

    private String username;

    private String email;

    private String roleName;

    private String status;

}