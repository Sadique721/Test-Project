package com.savbill.integrationsystem.pojo;

import lombok.Data;

import java.util.List;

@Data
public class NMSServiceActivationDTO {
    List<ProductParameterDefaultValueMappingDTO> parameters;
    List<CustInvParamsDto> custInvParams;
    Integer custId;
    Integer custServiceMapId;
    String configName;
    Integer customerServiceMappingId;
    String upstreamprofileuuid;
    String downstreamprofileuuid;
    String username;
    Integer mvnoId;
}
