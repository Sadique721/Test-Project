package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "tbltcustomerinventorymapping")
@EntityListeners(AuditableListener.class)
public class CustomerInventoryMapping extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long id;

    @Column(name = "quantity")
    Long qty;

    @ManyToOne(targetEntity = Product.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id",nullable = true)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    Customers customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    StaffUser staff;

    @Column(name = "inward_id")
    @DiffIgnore
    private Long inwardId;

    @Column(name = "assigned_date_time")
    LocalDateTime assignedDateTime;

    @Column(name = "mvno_id")
    @DiffIgnore
    private Integer mvnoId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "status")
    String status;

    @Column(name = "expiry_date_time")
    LocalDateTime expiryDateTime;

    @ManyToOne(targetEntity = StaffUser.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "next_approver", referencedColumnName = "staffid")
    StaffUser nextApprover;

//    @ManyToOne(targetEntity = TeamHierarchyMapping.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "team_hierarchy_mapping_id", referencedColumnName = "id")

    @Column(name = "team_hierarchy_mapping_id")
    @DiffIgnore
    Integer teamHierarchyMappingId;

    @OneToMany(targetEntity = InOutWardMACMapping.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "cust_inventory_mapping_id")
    @DiffIgnore
    List<InOutWardMACMapping> inOutWardMACMapping;

    @OneToMany(targetEntity = ExternalItemMacSerialMapping.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "cust_inventory_mapping_id")
    @DiffIgnore
    List<ExternalItemMacSerialMapping> externalItemMacSerialMappings;

    @Column(name = "previous_approve_id")
    @DiffIgnore
    Integer previousApproveId;

    @Column(name = "external_item_id")
    @DiffIgnore
    private Long externalItemId;

    @Column(name = "service_id")
    @DiffIgnore
    private Long serviceId;

    @Column(name = "custpack_id")
    @DiffIgnore
    private Long custPackId;

    @Column(name = "item_id")
    @DiffIgnore
    private Long itemId;

    @Column(name = "itemassemblyid")
    @DiffIgnore
    private Long itemAssemblyId;

    @Column(name = "connection_no")
    private String connectionNo;

    @Column(name = "is_invoice_created")
    private Boolean isInvoiceCreated = false;
    @Column(name = "plan_id")
    @DiffIgnore
    private Long planId;

    @Column(name = "replacement_reason")
    private String replacementReason;

    @Column(name = "mapping_ref_id")
    @DiffIgnore
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
    @DiffIgnore
    private Long chargeId;

    @Column(name = "plangroup_id")
    @DiffIgnore
    private Long planGroupId;

    @Column(name = "is_required_approval")
    @DiffIgnore
    private Boolean isRequiredApproval;

    @Column(name = "is_free")
    @DiffIgnore
    private Boolean isFree;

    @Column(name = "payment_owner_id")
    @DiffIgnore
    private Long paymentOwnerId;

    @Column(name = "non_seri_remark")
    @DiffIgnore
    private String nonSerializedItemRemark;

    @Transient
    private String customerFirstName;
    @Transient
    private String customerLastName;

    @Transient
    @DiffIgnore
    private String itemwarranty;
    @Transient
    private LocalDateTime expDate;

    @Transient
    private String serviceAreaName;

    @Column(name = "ezybill_stock_id")
    @DiffIgnore
    private String ezyBillStockId;

    @Column(name = "billable_cust_id")
    @DiffIgnore
    private Long billabecustId;

    @Column(name = "pairstatus")
    @DiffIgnore
    private String pairStatus;

    @Column(name = "filename")
    private String filename;

    @Column(name = "uniquename")
    private String uniquename;

    @Column(name = "optical_power_range")
    private String opticalPowerRange;

    @Column(name = "inventory_job_type", nullable = true)
    private String inventoryJobType;

    @Column(name = "nature", nullable = true)
    private String nature;


    @Transient
    @DiffIgnore
    private String flag;


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

}


