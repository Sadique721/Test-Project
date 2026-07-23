package com.savbill.integrationsystem.CustomerInventoryMapping;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "tblmcustomer_inventory_mapping")
public class CustomerInventoryMappingEntity  {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long id;

    @Column(name = "quantity")
    Long qty;

    @Column(name = "product_id")
    Integer productId;
    @Column(name = "customer_id")
    private Integer custid;

    @Column(name = "staff_id")
    Integer staff;

    @Column(name = "inward_id")
    private Long inwardId;

    @Column(name = "assigned_date_time")
    LocalDateTime assignedDateTime;

    @Column(name = "mvno_id")
    private Integer mvnoId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "status")
    String status;

    @Column(name = "expiry_date_time")
    LocalDateTime expiryDateTime;

    @Column(name = "next_approver")
    Integer nextApprover;

    //    @ManyToOne(targetEntity = TeamHierarchyMapping.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "team_hierarchy_mapping_id", referencedColumnName = "id")
    @Column(name = "team_hierarchy_mapping_id")
    Integer teamHierarchyMappingId;

//    @Column(name = "cust_inventory_mapping_id")
//     Integer inOutWardMACMapping;

//    @Column(name = "external_item_id")
//    Integer externalItemMacSerialMappings;

    @Column(name = "previous_approve_id")
    Integer previousApproveId;

    @Column(name = "external_item_id")
    private Long externalItemId;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "custpack_id")
    private Long custPackId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "itemassemblyid")
    private Long itemAssemblyId;

    @Column(name = "connection_no")
    private String connectionNo;

    @Column(name = "is_invoice_created")
    private Boolean isInvoiceCreated = false;
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "replacement_reason")
    private String replacementReason;

    @Column(name = "mapping_ref_id")
    private Long mapping_ref_id;

    @Column(name = "remark")
    private String approvalRemark;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "new_amount")
    private Double newAmount;

    @Column(name = "offer_price")
    private Double offerPrice;

    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg;

    @Column(name = "charge_id")
    private Long chargeId;

    @Column(name = "plangroup_id")
    private Long planGroupId;

    @Column(name = "is_required_approval")
    private Boolean isRequiredApproval;

    @Column(name = "is_free")
    private Boolean isFree;

    @Column(name = "payment_owner_id")
    private Long paymentOwnerId;

    @Transient
    private String customerFirstName;
    @Transient
    private String customerLastName;

    @Transient
    private String itemwarranty;
    @Transient
    private LocalDateTime expDate;

    @Transient
    private String serviceAreaName;

    @Column(name = "ezybill_stock_id")
    private String ezyBillStockId;

    @Column(name = "billable_cust_id")
    private Long billabecustId;

    @Column(name = "pairstatus")
    private String pairStatus;

    @Transient
    private String flag;
    @Transient
    private boolean isUpdate;
    CustomerInventoryMappingEntity(Map message){
        if(message.get("ids") != null){
            List<?> ids = (List<?>)message.get("ids");
            this.id = Long.valueOf(ids.get(0).toString());
        } else if (message.get("id") != null){
            this.id = Long.valueOf(message.get("id").toString());
        }
        if(message.get("qty") != null){
            this.qty = Long.valueOf(message.get("qty").toString());
        }
        if(message.get("productId") != null){
            this.productId = Integer.valueOf(message.get("productId").toString());
        }
        if(message.get("custId") != null){
            this.custid = Integer.valueOf(message.get("custId").toString());
        }
        if(message.get("staffId") != null){
            this.staff = Integer.valueOf(message.get("staffId").toString());
        }
        if(message.get("inwardId") != null){
            this.inwardId = Long.valueOf(message.get("inwardId").toString());
        }
        if(message.get("mvnoId") != null){
            this.mvnoId = Integer.valueOf(message.get("mvnoId").toString());
        }
        if(message.get("isDeleted") != null){
            this.isDeleted = Boolean.parseBoolean(message.get("id").toString());
        }
        if(message.get("status") != null){
            this.status = (message.get("status").toString());
        }
        if(message.get("nextApprover") != null){
            this.nextApprover = Integer.valueOf(message.get("nextApprover").toString());
        }
        if(message.get("teamHierarchyMappingId") != null){
            this.teamHierarchyMappingId = Integer.valueOf(message.get("teamHierarchyMappingId").toString());
        }
        if(message.get("previousApproveId") != null){
            this.previousApproveId = Integer.valueOf(message.get("previousApproveId").toString());
        }
        if(message.get("externalItemId") != null){
            this.externalItemId = Long.valueOf(message.get("externalItemId").toString());
        }
        if(message.get("serviceId") != null){
            this.serviceId = Long.valueOf(message.get("serviceId").toString());
        }
        if(message.get("custPackId") != null){
            this.custPackId = Long.valueOf(message.get("custPackId").toString());
        }
        if(message.get("itemId") != null){
            this.itemId = Long.valueOf(message.get("itemId").toString());
        }
        if(message.get("itemAssemblyId") != null){
            this.itemAssemblyId = Long.valueOf(message.get("itemAssemblyId").toString());
        }
        if(message.get("connectionNo") != null){
            this.connectionNo = (message.get("connectionNo").toString());
        }
        if(message.get("isInvoiceCreated") != null){
            this.isInvoiceCreated = Boolean.parseBoolean(message.get("isInvoiceCreated").toString());
        }
        if(message.get("planId") != null){
            this.planId = Long.valueOf(message.get("planId").toString());
        }
        if(message.get("replacementReason") != null){
            this.replacementReason = (message.get("replacementReason").toString());
        }
        if(message.get("mapping_ref_id") != null){
            this.mapping_ref_id = Long.valueOf(message.get("mapping_ref_id").toString());
        }
        if(message.get("approvalRemark") != null){
            this.approvalRemark = (message.get("approvalRemark").toString());
        }
        if(message.get("discount") != null){
            this.discount = Double.valueOf(message.get("discount").toString());
        }
        if(message.get("billTo") != null){
            this.billTo = (message.get("billTo").toString());
        }
        if(message.get("newAmount") != null){
            this.newAmount = Double.valueOf(message.get("newAmount").toString());
        }
        if(message.get("offerPrice") != null){
            this.offerPrice = Double.valueOf(message.get("offerPrice").toString());
        }
        if(message.get("isInvoiceToOrg") != null){
            this.isInvoiceToOrg = Boolean.parseBoolean(message.get("isInvoiceToOrg").toString());
        }
        if(message.get("chargeId") != null){
            this.chargeId = Long.valueOf(message.get("chargeId").toString());
        }
        if(message.get("planGroupId") != null){
            this.planGroupId = Long.valueOf(message.get("planGroupId").toString());
        }
        if(message.get("isRequiredApproval") != null){
            this.isRequiredApproval = Boolean.parseBoolean(message.get("id").toString());
        }
        if(message.get("isFree") != null){
            this.isFree = Boolean.parseBoolean(message.get("isFree").toString());
        }
        if(message.get("paymentOwnerId") != null){
            this.paymentOwnerId = Long.valueOf(message.get("paymentOwnerId").toString());
        }
        if(message.get("ezyBillStockId") != null){
            this.ezyBillStockId = (message.get("ezyBillStockId").toString());
        }if(message.get("billabecustId") != null){
            this.billabecustId = Long.valueOf(message.get("billabecustId").toString());
        }
        if(message.get("pairStatus") != null){
            this.pairStatus = (message.get("pairStatus").toString());
        }
        if(message.get("id") != null){
            this.id = Long.valueOf(message.get("id").toString());
        }
        if(message.get("isUpdate") != null){
            this.isUpdate = Boolean.parseBoolean(message.get("isUpdate").toString());
        }
        if(message.get("assignedDateTime") != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.assignedDateTime = LocalDateTime.parse(message.get("assignedDateTime").toString(), formatter);
        }
        if(message.get("expiryDateTime") != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.expiryDateTime = LocalDateTime.parse(message.get("expiryDateTime").toString(), formatter);
        }
        if(message.get("expDate") != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.expDate = LocalDateTime.parse(message.get("expDate").toString(), formatter);
        }
    }
}


