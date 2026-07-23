package com.savbill.revenuemanagement.core.entity.inventory;

import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.rabbitmq.messages.inventory.CustomerInventoryRevenueMessage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "tbltcustomerinventorymapping")
@EntityListeners(AuditableListener.class)
public class CustomerInventoryMapping extends Auditable implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long id;

    @Column(name = "quantity")
    Long qty;

//    @ManyToOne(targetEntity = Product.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "product_id", referencedColumnName = "product_id",nullable = true)
//    Product product;

    @Column(name = "product_id")
    Long productId;

//    @ManyToOne(targetEntity = Customers.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "customer_id", referencedColumnName = "custid")
//    Customers customer;

    @Column(name = "customer_id")
    Long customerId;

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

//    @ManyToOne(targetEntity = TeamHierarchyMapping.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "team_hierarchy_mapping_id", referencedColumnName = "id")
    @Column(name = "team_hierarchy_mapping_id")
    Integer teamHierarchyMappingId;

//    @OneToMany(targetEntity = InOutWardMACMapping.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
//    @JoinColumn(name = "cust_inventory_mapping_id")
//    List<InOutWardMACMapping> inOutWardMACMapping;

//    @OneToMany(targetEntity = ExternalItemMacSerialMapping.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
//    @JoinColumn(name = "cust_inventory_mapping_id")
//    List<ExternalItemMacSerialMapping> externalItemMacSerialMappings;

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

    @Column(name = "non_seri_remark")
    private String nonSerializedItemRemark;

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
    private  Integer ceratedById;

    @Transient
    private  Integer loggedInUserId;


    @Override
    public Long getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;

    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public CustomerInventoryMapping(CustomerInventoryRevenueMessage message) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> inventoryData = message.getCustomerInventoryData();
        if(inventoryData.containsKey("id") && inventoryData.get("id") != null)
            this.id = Long.valueOf(inventoryData.get("id").toString());
        if(inventoryData.containsKey("qty") && inventoryData.get("qty") != null)
            this.qty = Long.valueOf(inventoryData.get("qty").toString());
        if(inventoryData.containsKey("productId") && inventoryData.get("productId") != null)
            this.productId = Long.valueOf(inventoryData.get("productId").toString());
        if(inventoryData.containsKey("custId") && inventoryData.get("custId") != null)
            this.customerId = Long.valueOf(inventoryData.get("custId").toString());
        if(inventoryData.containsKey("inwardId") && inventoryData.get("inwardId") != null)
            this.inwardId = Long.valueOf(inventoryData.get("inwardId").toString());
        if(inventoryData.containsKey("assignedDateTime") && inventoryData.get("assignedDateTime") != null)
            this.assignedDateTime = LocalDateTime.parse(inventoryData.get("assignedDateTime").toString(), format);
        if(inventoryData.containsKey("mvnoId") && inventoryData.get("mvnoId") != null)
            this.mvnoId = Integer.valueOf(inventoryData.get("mvnoId").toString());
        if(inventoryData.containsKey("isDeleted") && inventoryData.get("isDeleted") != null)
            this.isDeleted = Boolean.valueOf(inventoryData.get("isDeleted").toString());
        if(inventoryData.containsKey("status") && inventoryData.get("status") != null)
            this.status = inventoryData.get("status").toString();
        if(inventoryData.containsKey("expiryDateTime") && inventoryData.get("expiryDateTime") != null)
            this.expiryDateTime = LocalDateTime.parse(inventoryData.get("expiryDateTime").toString(), format);
//        if(inventoryData.containsKey("id"))
//            this.teamHierarchyMappingId = teamHierarchyMappingId;
//        if(inventoryData.containsKey("id"))
//            this.previousApproveId = previousApproveId;
        if(inventoryData.containsKey("externalItemId") && inventoryData.get("externalItemId") != null)
            this.externalItemId = Long.valueOf(inventoryData.get("externalItemId").toString());
        if(inventoryData.containsKey("serviceId") && inventoryData.get("serviceId") != null)
            this.serviceId = Long.valueOf(inventoryData.get("serviceId").toString());
        if(inventoryData.containsKey("custPackId") && inventoryData.get("custPackId") != null)
            this.custPackId = Long.valueOf(inventoryData.get("custPackId").toString());
        if(inventoryData.containsKey("itemId") && inventoryData.get("itemId") != null)
            this.itemId = Long.valueOf(inventoryData.get("itemId").toString());
        if(inventoryData.containsKey("itemAssemblyId") && inventoryData.get("itemAssemblyId") != null)
            this.itemAssemblyId = Long.valueOf(inventoryData.get("itemAssemblyId").toString());
        if(inventoryData.containsKey("connectionNo") && inventoryData.get("connectionNo") != null)
            this.connectionNo = inventoryData.get("connectionNo").toString();
        if(inventoryData.containsKey("isInvoiceCreated") && inventoryData.get("isInvoiceCreated") != null)
            this.isInvoiceCreated = Boolean.valueOf(inventoryData.get("isInvoiceCreated").toString());
        if(inventoryData.containsKey("planId") && inventoryData.get("planId") != null)
            this.planId = Long.valueOf(inventoryData.get("planId").toString());
        if(inventoryData.containsKey("replacementReason") && inventoryData.get("replacementReason") != null)
            this.replacementReason = inventoryData.get("replacementReason").toString();
        if(inventoryData.containsKey("mapping_ref_id") && inventoryData.get("mapping_ref_id") != null)
            this.mapping_ref_id = Long.valueOf(inventoryData.get("mapping_ref_id").toString());
        if(inventoryData.containsKey("approvalRemark") && inventoryData.get("approvalRemark") != null)
            this.approvalRemark = inventoryData.get("approvalRemark").toString();
        if(inventoryData.containsKey("discount") && inventoryData.get("discount") != null)
            this.discount = Double.valueOf(inventoryData.get("discount").toString());
        if(inventoryData.containsKey("billTo") && inventoryData.get("billTo") != null)
            this.billTo = inventoryData.get("billTo").toString();
        if(inventoryData.containsKey("newAmount") && inventoryData.get("newAmount") != null)
            this.newAmount = Double.valueOf(inventoryData.get("newAmount").toString());
        if(inventoryData.containsKey("offerPrice") && inventoryData.get("offerPrice") != null)
            this.offerPrice = Double.valueOf(inventoryData.get("offerPrice").toString());
        if(inventoryData.containsKey("isInvoiceToOrg") && inventoryData.get("isInvoiceToOrg") != null)
            this.isInvoiceToOrg = Boolean.valueOf(inventoryData.get("isInvoiceToOrg").toString());
        if(inventoryData.containsKey("chargeId") && inventoryData.get("chargeId") != null)
            this.chargeId = Long.valueOf(inventoryData.get("chargeId").toString());
        if(inventoryData.containsKey("planGroupId") && inventoryData.get("planGroupId") != null)
            this.planGroupId = Long.valueOf(inventoryData.get("planGroupId").toString());
        if(inventoryData.containsKey("isRequiredApproval") && inventoryData.get("isRequiredApproval") != null)
            this.isRequiredApproval = Boolean.valueOf(inventoryData.get("isRequiredApproval").toString());
        if(inventoryData.containsKey("isFree") && inventoryData.get("isFree") != null)
            this.isFree = Boolean.valueOf(inventoryData.get("isFree").toString());
        if(inventoryData.containsKey("paymentOwnerId") && inventoryData.get("paymentOwnerId") != null)
            this.paymentOwnerId = Long.valueOf(inventoryData.get("paymentOwnerId").toString());
        if(inventoryData.containsKey("ezyBillStockId") && inventoryData.get("ezyBillStockId") != null)
            this.ezyBillStockId = inventoryData.get("ezyBillStockId").toString();
        if(inventoryData.containsKey("billabecustId") && inventoryData.get("billabecustId") != null)
            this.billabecustId = Long.valueOf(inventoryData.get("billabecustId").toString());
        if(inventoryData.containsKey("pairStatus") && inventoryData.get("pairStatus") != null)
            this.pairStatus = inventoryData.get("pairStatus").toString();
        if(inventoryData.containsKey("createdById") && inventoryData.get("createdById") != null )
            this.setCreatedById(Integer.valueOf(inventoryData.get("createdById").toString()));
        if(inventoryData.containsKey("staffId") && inventoryData.get("staffId") != null )
            this.setLoggedInUserId(Integer.valueOf(inventoryData.get("staffId").toString()));

    }
}


