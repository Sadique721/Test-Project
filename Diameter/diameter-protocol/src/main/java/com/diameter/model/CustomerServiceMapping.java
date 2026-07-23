package com.diameter.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbltcustomerservicemapping")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerServiceMapping extends Auditable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // aligns with DB auto increment
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "custid", nullable = false)
    private Integer custId;

    @Column(name = "serviceid")
    private Long serviceId;

    @Column(name = "leasecircuitid")
    private Long leaseCircuitId;

    @Column(name = "connection_no", length = 255)
    private String connectionNo;

    @Column(name = "nickname", length = 255)
    private String nickName;

    @Column(name = "invoice_format", length = 100)
    private String invoiceFormat;

    @Column(name = "invoice_type", length = 100)
    private String invoiceType;

    @Column(name = "lease_circuit_name", length = 255)
    private String leaseCircuitName;

    @Column(name = "circuit_status", length = 100)
    private String circuitStatus;

    @Column(name = "caf_no")
    private Long cafNo;

    @Column(name = "upload_caf", length = 255)
    private String uploadCAF;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "account_number")
    private Long accountNumber;

    @Column(name = "type_of_link", length = 100)
    private String typeOfLink;

    @Column(name = "link_installation_date")
    private LocalDate linkInstallationDate;

    @Column(name = "link_acceptance_date")
    private LocalDate linkAcceptanceDate;

    @Column(name = "purchase_order_date")
    private LocalDate purchaseOrderDate;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "distance")
    private Long distance;

    @Column(name = "distance_unit", length = 50)
    private String distanceUnit;

    @Column(name = "bandwidth")
    private Long bandwidth;

    @Column(name = "uploadQOS", length = 100)
    private String uploadQos;

    @Column(name = "downloadQOS", length = 100)
    private String downloadQos;

    @Column(name = "link_router_location", length = 255)
    private String linkRouterLocation;

    @Column(name = "link_port_type", length = 100)
    private String linkPortType;

    @Column(name = "link_router_ip")
    private Long linkRouterIp;

    @Column(name = "link_port_on_router", length = 100)
    private String linkPortOnRouter;

    @Column(name = "vlan_id")
    private Long vlanId;

    @Column(name = "bandwidth_type", length = 100)
    private String bandwidthType;

    @Column(name = "link_router_name", length = 255)
    private String linkRouterName;

    @Column(name = "circuit_billing_id")
    private Long circuitBillingId;

    @Column(name = "pop", length = 100)
    private String pop;

    @Column(name = "associated_level", length = 100)
    private String associatedLevel;

    @Column(name = "location_level1")
    private String locationLevel1;

    @Column(name = "location_level2")
    private String locationLevel2;

    @Column(name = "location_level3")
    private String locationLevel3;

    @Column(name = "location_level4")
    private String locationLevel4;

    @Column(name = "base_station_id1")
    private Long baseStationId1;

    @Column(name = "base_station_id2")
    private Long baseStationId2;

    @Column(name = "termination_address", length = 500)
    private String terminationAddress;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "billing_cycle", length = 50)
    private String billingCycle;

    @Column(name = "billing_type", length = 50)
    private String billingType;

    @Column(name = "billable", length = 50)
    private String billable;

    @Column(name = "billing_group", length = 100)
    private String billingGroup;

    @Column(name = "payable", length = 50)
    private String payable;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "s_discount_type", length = 50)
    private String discountType;

    @Column(name = "s_discount")
    private Double discount;

    @Column(name = "discount_expiry_date")
    private LocalDate discountExpiryDate;

    @Column(name = "new_discount_type", length = 50)
    private String newDiscountType;

    @Column(name = "new_discount")
    private Double newDiscount;

    @Column(name = "new_discount_expiry_date")
    private LocalDate newDiscountExpiryDate;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "hold_service_date")
    private LocalDateTime serviceHoldDate;

    @Column(name = "resume_service_date")
    private LocalDateTime serviceResumeDate;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID")
    private Integer mvnoId;

    @Column(name = "buid")
    private Long buId;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "imsi")
    private String imsi;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

}
