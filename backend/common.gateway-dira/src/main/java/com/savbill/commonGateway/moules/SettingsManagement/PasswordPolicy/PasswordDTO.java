package com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy;

import com.savbill.commonGateway.core.data.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO extends Auditable {

    private Long id;
    private String name;
    private String status;
    private Long min_length;
    private Long max_length;
    private Long expiration_days;
    private Long disable_recycling_prevention;
    private Long disable_account_lockout;
    private String pattern;
    private String pattern_description;
    private Integer mvnoId;
    private Boolean isDelete = false;
    private Boolean isNotificationRequired = false;

}
