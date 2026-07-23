package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.Productplanmappingdto;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.PostpaidPlanCharge.PostpaidPlanCharge;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SavePlanSharedDataMessage {
    private Integer id;
    private String name;
    private String displayName;
    private String status;
    private String planStatus;
    private Integer mvnoId;
    private Integer serviceId;
    private Long buId;
    private String plantype;
    private List<PostpaidPlanCharge> chargeList;
    private String planGroup;
    private Boolean isDelete;
    private List<ServiceArea> serviceAreaNameList;
    private List<Productplanmappingdto> productplanmappingList;
    private Integer createdById;
    private Integer lastModifiedById;
    private Boolean isApprove;
}
