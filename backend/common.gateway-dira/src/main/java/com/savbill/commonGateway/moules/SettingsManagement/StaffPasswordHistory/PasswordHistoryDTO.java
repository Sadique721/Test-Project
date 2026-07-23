package com.savbill.commonGateway.moules.SettingsManagement.StaffPasswordHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordHistoryDTO {

    private Long id;
    private Long passwordAttemptNumber;
    private String password;
    private Integer staffId;
    private String uuid;
}
