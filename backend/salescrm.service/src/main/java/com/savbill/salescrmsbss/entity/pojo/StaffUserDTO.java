package com.savbill.salescrmsbss.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StaffUserDTO {
    private Integer id;
    private String firstname;
    private String lastname;
    private List<Long> businessUnitIds;

    public StaffUserDTO(Integer id, String firstname, String lastname, List<Long> businessUnitIds) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.businessUnitIds = businessUnitIds;
    }
}
