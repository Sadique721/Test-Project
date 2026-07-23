package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;


import com.savbill.ticketmanagement.core.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdateServicesSharedDataMessage {

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
}
