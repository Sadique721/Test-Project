package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StaffUserDropdownDTO {

    private Integer id;

    private String username;

    private String firstname;

    private String lastname;

    private String fullName;

    private String phone;

    public StaffUserDropdownDTO(Integer id, String username, String firstname, String lastname) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fullName = firstname+" "+lastname;
    }



}

