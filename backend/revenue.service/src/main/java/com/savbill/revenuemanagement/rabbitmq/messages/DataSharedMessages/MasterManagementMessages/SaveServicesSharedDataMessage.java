package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages;


import com.savbill.revenuemanagement.productmanagement.ServiceParameterMapping.domain.ServiceParamMapping;

import lombok.Data;

import java.util.List;

@Data
public class SaveServicesSharedDataMessage {

    private Integer id;
    private String name;
    private String icname;
    private String iccode;
    private Integer mvnoId;
    private Long buId;
    private Boolean isQoSV;
    private String expiry;
    private String ledgerId;
    private Boolean is_dtv;
    private Long investmentid;
//    private List<ProductCategory> productCategories = new ArrayList<>();
//    List<ServiceParamMapping> serviceParamMappingList;
    private Boolean feasibility;
    private Boolean poc;
    private Boolean installation;
    private Boolean provisioning;
    private Boolean isPriceEditable;
    private Long feasibilityTeamId;
    private Long pocTeamId;
    private Long installationTeamId;
    private Long provisioningTeamId;
    private Boolean isDeleted;
    private List<ServiceParamMapping> serviceParamMappingList;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
