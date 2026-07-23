package com.savbill.notification.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffCustomDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String mobileNumber;
}
