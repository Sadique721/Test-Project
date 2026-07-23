package com.savbill.integrationsystem.rabbitmq;

import com.savbill.integrationsystem.CustomerInventoryMapping.CustomerInventoryMappingEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerInventoryMappingMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;

    private Map<String,Object> customerInventoryData = new HashMap<>();
    public CustomerInventoryMappingMessage(CustomerInventoryMappingEntity customerInventoryMapping, String message, boolean isUpdate) {
        this.setMessage(message);
        this.setMessageDate(new Date());
        this.messageId = UUID.randomUUID().toString();
        customerInventoryData.put("id",customerInventoryMapping.getId());
        customerInventoryData.put("qty",customerInventoryMapping.getQty());
        customerInventoryData.put("productId",customerInventoryMapping.getProductId());
        customerInventoryData.put("custId",customerInventoryMapping.getCustid());
        customerInventoryData.put("staffId",customerInventoryMapping.getStaff());
        customerInventoryData.put("inwardId",customerInventoryMapping.getInwardId());
        customerInventoryData.put("mvnoId",customerInventoryMapping.getMvnoId());
        customerInventoryData.put("isDeleted",customerInventoryMapping.getIsDeleted());
        customerInventoryData.put("status",customerInventoryMapping.getStatus());
        customerInventoryData.put("nextApprover",customerInventoryMapping.getNextApprover());
        customerInventoryData.put("teamHierarchyMappingId",customerInventoryMapping.getTeamHierarchyMappingId());
        customerInventoryData.put("previousApproveId",customerInventoryMapping.getPreviousApproveId());
        customerInventoryData.put("externalItemId",customerInventoryMapping.getExternalItemId());
        customerInventoryData.put("serviceId",customerInventoryMapping.getServiceId());
        customerInventoryData.put("custPackId",customerInventoryMapping.getCustPackId());
        customerInventoryData.put("itemId",customerInventoryMapping.getItemId());
        customerInventoryData.put("itemAssemblyId",customerInventoryMapping.getItemAssemblyId());
        customerInventoryData.put("connectionNo",customerInventoryMapping.getConnectionNo());
        customerInventoryData.put("isInvoiceCreated",customerInventoryMapping.getIsInvoiceCreated());
        customerInventoryData.put("planId",customerInventoryMapping.getPlanId());
        customerInventoryData.put("replacementReason",customerInventoryMapping.getReplacementReason());
        customerInventoryData.put("mapping_ref_id",customerInventoryMapping.getMapping_ref_id());
        customerInventoryData.put("approvalRemark",customerInventoryMapping.getApprovalRemark());
        customerInventoryData.put("discount",customerInventoryMapping.getDiscount());
        customerInventoryData.put("billTo",customerInventoryMapping.getBillTo());
        customerInventoryData.put("newAmount",customerInventoryMapping.getNewAmount());
        customerInventoryData.put("offerPrice",customerInventoryMapping.getOfferPrice());
        customerInventoryData.put("isInvoiceToOrg",customerInventoryMapping.getIsInvoiceToOrg());
        customerInventoryData.put("chargeId",customerInventoryMapping.getChargeId());
        customerInventoryData.put("planGroupId",customerInventoryMapping.getPlanGroupId());
        customerInventoryData.put("isRequiredApproval",customerInventoryMapping.getIsRequiredApproval());
        customerInventoryData.put("isFree",customerInventoryMapping.getIsFree());
        customerInventoryData.put("paymentOwnerId",customerInventoryMapping.getPaymentOwnerId());
        customerInventoryData.put("ezyBillStockId",customerInventoryMapping.getEzyBillStockId());
        customerInventoryData.put("billabecustId",customerInventoryMapping.getBillabecustId());
        customerInventoryData.put("pairStatus",customerInventoryMapping.getPairStatus());
        customerInventoryData.put("assignedDateTime",customerInventoryMapping.getAssignedDateTime());
        customerInventoryData.put("expiryDateTime",customerInventoryMapping.getExpiryDateTime());
        customerInventoryData.put("expDate",customerInventoryMapping.getExpDate());
        customerInventoryData.put("isUpdate", isUpdate);
    }
}
