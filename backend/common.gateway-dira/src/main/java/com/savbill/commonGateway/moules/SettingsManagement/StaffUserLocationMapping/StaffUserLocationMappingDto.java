package com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffUserLocationMappingDto {
    private Long id;
    private Long staffId;
    private Long locationId;
    private String locationName;
}
