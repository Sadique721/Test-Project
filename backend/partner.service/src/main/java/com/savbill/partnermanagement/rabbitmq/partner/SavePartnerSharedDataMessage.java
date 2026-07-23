package com.savbill.partnermanagement.rabbitmq.partner;

import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import lombok.Data;

import java.util.List;

@Data
public class SavePartnerSharedDataMessage {

    private Integer id;
    private String name;
    private String status;
    private Integer city;
    private Integer state;
    private Integer country;
    private String pincode;
    private String email;
    private String partnerType;
    private List<ServiceArea> serviceAreaList;
    private Long parentPartnerId;
    private Boolean isDelete;
    private Integer mvnoId;
    private Long buId;
    private Long branch;
    private Integer createdById;
    private Integer lastModifiedById;
}
