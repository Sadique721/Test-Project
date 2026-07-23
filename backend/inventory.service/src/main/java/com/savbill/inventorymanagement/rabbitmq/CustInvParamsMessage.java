package com.savbill.inventorymanagement.rabbitmq;

import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.CustInvParamsDto;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CustInvParamsMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private Long custSerMapId;
    private String updatedValues;
    private Boolean isUpdate;
    private Long custId;
    private Long custInvId;

    private List<CustInvParamsDto> custInvParams;

    public CustInvParamsMessage() {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Inventory Params Data";
    }
}
