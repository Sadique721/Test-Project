package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAreaDTOProjection {
    private Long id;
    private String name;
    private String status;
    private Integer mvnoId;
    private String latitude;
    private String longitude;
    private String serviceAreaType;
}
