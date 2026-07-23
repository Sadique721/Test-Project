package com.savbill.integrationsystem.InventoryItem;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "tblmapproveinventoryserializeditem")
public class ApproveInventoryItem {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mac")
    private String macAddress;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "item_condition")
    private String condition;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "current_inward_id")
    private Long currentInwardId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "warranty_period", nullable = false)
    private Integer warrantyPeriod;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "current_inward_type")
    private String currentInwardType;

    @Column(name = "item_status")
    private String itemStatus;

    @Column(name = "remaining_days")
    private  String remainingDays;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "ownership_type")
    private String ownershipType;

    @Column(name = "external_item_id")
    private Long externalItemId;

    @Column(name = "intransiant_warrenty")
    private String intransiantWarrenty;


    @Column(name = "intransiant_ownership")
    private String intransiantOwnership;

    @Column(name = "intransiant_warrenty_status")
    private String intransiantWarrentyStatus;

    @Column(name="expiry_date")
    private LocalDateTime expireDate;

    @Column(name="intransiant_expiry_date")
    private LocalDateTime intransiantexpireDate;


    public ApproveInventoryItem(Map message) {
        if (message.get("id") != null) {
            this.id = Long.valueOf(message.get("id").toString());
        }
        if (message.get("name") != null) {
            this.name = message.get("name").toString();
        }
        if (message.get("macAddress") != null) {
            this.macAddress = message.get("macAddress").toString();
        }
        if (message.get("serialNumber") != null) {
            this.serialNumber = message.get("serialNumber").toString();
        }
        if (message.get("mvnoId") != null) {
            this.mvnoId = Integer.valueOf(message.get("mvnoId").toString());
        }
        if (message.get("condition") != null) {
            this.condition = message.get("condition").toString();
        }
        if (message.get("productId") != null) {
            this.productId = Long.valueOf(message.get("productId").toString());
        }
        if (message.get("currentInwardId") != null) {
            this.currentInwardId = Long.valueOf(message.get("currentInwardId").toString());
        }
        if (message.get("ownerId") != null) {
            this.ownerId = Long.valueOf(message.get("ownerId").toString());
        }
        if (message.get("ownerType") != null) {
            this.ownerType = message.get("ownerType").toString();
        }
        if (message.get("warrantyPeriod") != null) {
            this.warrantyPeriod = Integer.valueOf(message.get("warrantyPeriod").toString());
        }
        if (message.get("warranty") != null) {
            this.warranty = message.get("warranty").toString();
        }
        if (message.get("currentInwardType") != null) {
            this.currentInwardType = message.get("currentInwardType").toString();
        }
        if (message.get("itemStatus") != null) {
            this.itemStatus = message.get("itemStatus").toString();
        }
        if (message.get("remainingDays") != null) {
            this.remainingDays = message.get("remainingDays").toString();
        }
        if (message.get("isDeleted") != null) {
            this.isDeleted = Boolean.valueOf(message.get("isDeleted").toString());
        }
        if (message.get("ownershipType") != null) {
            this.ownershipType = message.get("ownershipType").toString();
        }
        if (message.get("externalItemId") != null) {
            this.externalItemId = Long.valueOf(message.get("externalItemId").toString());
        }
        if (message.get("intransiantWarrenty") != null) {
            this.intransiantWarrenty = message.get("intransiantWarrenty").toString();
        }
        if (message.get("intransiantOwnership") != null) {
            this.intransiantOwnership = message.get("intransiantOwnership").toString();
        }
        if (message.get("intransiantWarrentyStatus") != null) {
            this.intransiantWarrentyStatus = message.get("intransiantWarrentyStatus").toString();
        }
        if (message.get("expireDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.expireDate = LocalDateTime.parse(message.get("expireDate").toString(), formatter);
        }
        if (message.get("intransiantexpireDate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.intransiantexpireDate = LocalDateTime.parse(message.get("intransiantexpireDate").toString(), formatter);
        }
    }
}
