package com.savbill.integrationsystem.nms.entity;

import com.savbill.integrationsystem.pojo.CustInvParamsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UuidDataDTO {

    Integer customerServiceMappingId;
    String uuid;
    String configName;
    String userName;
    List<CustInvParamsDto> custInvParamsDtoList = new ArrayList<>();
    String cdataUuid;
    String cdataTemplate;

    public UuidDataDTO(Integer customerServiceMappingId, String cdataUuid, String cdataTemplate) {
        this.customerServiceMappingId = customerServiceMappingId;
        this.cdataUuid = cdataUuid;
        this.cdataTemplate = cdataTemplate;
    }
}
