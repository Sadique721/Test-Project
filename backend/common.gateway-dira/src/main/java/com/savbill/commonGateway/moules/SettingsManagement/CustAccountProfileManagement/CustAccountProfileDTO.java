package com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustAccountProfileDTO {

    private Long id;
    private String name;
    private String prefix;
    private String type;
    private String startFrom;
    private boolean year;
    private boolean month;
    private boolean day;
    private String status;
    private boolean isDelete;
    private Integer mvnoId;

}
