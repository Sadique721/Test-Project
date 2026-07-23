package com.savbill.partnermanagement.customers;
;
import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.security.spring.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbltcustomerservicemapping")
@EntityListeners(AuditableListener.class)
public class CustomerServiceMapping extends Auditable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "custid", nullable = false, length = 40)
    private Integer custId;

    @Column(name = "serviceid", nullable = false, length = 40)
    private Long serviceId;

    @Column(name = "leasecircuitid", nullable = false, length = 40)
    private Long leaseCircuitId;
    @Column(name = "connection_no")
    private String connectionNo;

    @Column(name = "nickname")
    private String nickName;

    @Transient
    private LocalDate stopServiceDate;

    @Column(name ="is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

//    @OneToOne
//    @JoinColumn(name = "purchaseorder_id")
//    private PurchaseOrder purchaseorder;

    @Column(name = "invoice_format")
    private String invoiceformat;

    @Column(name = "invoice_type")
    private String invoiceType;

    @Column(name = "lease_circuit_name")
    private String leaseCircuitName;
    @Column(name = "circuit_status")
    private String circuitStatus;
    @Column(name = "caf_no")
    private Long cafNo;
    @Column(name = "upload_caf")
    private String uploadCAF;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "account_number")
    private Long accountNumber;
    @Column(name = "type_of_link")
    private String typeOfLink;
    @Column(name = "link_installation_date")
    private LocalDate linkInstallationDate;
    @Column(name = "link_acceptance_date")
    private LocalDate linkAcceptanceDate;
    @Column(name = "purchase_order_date")
    private LocalDate purchaseOrderDate;
//    @Column(name = "circle_name")
//    private String circleName;
//    @Column(name = "order_login_date")
//    private LocalDate orderLoginDate;
    @Column(name = "partner_id")
    private Long partner;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    @Column(name = "distance")
    private Long distance;
    @Column(name = "distance_unit")
    private String distanceUnit;
    @Column(name = "bandwidth")
    private Long bandwidth;
    @Column(name = "uploadQOS")
    private String uploadQOS;
    @Column(name = "downloadQOS")
    private String downloadQOS;
    @Column(name = "link_router_location")
    private String linkRouterLocation;
    @Column(name = "link_port_type")
    private String linkPortType;
    @Column(name = "link_router_ip")
    private Long linkRouterIp;
    @Column(name = "link_port_on_router")
    private String linkPortOnRouter;
    @Column(name = "vlan_id")
    private Long vLANId;
    @Column(name = "bandwidth_type")
    private String bandwidthType;
    @Column(name = "link_router_name")
    private String linkRouterName;
    @Column(name = "circuit_billing_id")
    private Long circuitBillingId;
    @Column(name = "pop")
    private String pop;
    @Column(name = "associated_level")
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
//    @Column(name = "organisation_circle")
//    private String organisationCircle;
//    @Column(name = "termination_circle")
//    private String terminationCircle;
//    @Column(name = "organisation_address")
//    private String organisationAddress;
    @Column(name = "termination_address")
    private String terminationAddress;
//    @Column(name = "organisation_address2")
//    private String organisationAddress2;
//    @Column(name = "termination_address2")
//    private String terminationAddress2;
    @Column(name = "note")
    private String note;
    @Column(name = "contact_person")
    private String contactPerson;
    @Column(name = "contact_person1")
    private String contactPerson1;
//    @Column(name = "contact_person2")
//    private String contactPerson2;
    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "mobile_number1")
    private String mobileNumber1;
//    @Column(name = "mobile_number2")
//    private String mobileNumber2;
    @Column(name = "landline_number")
    private String landlineNumber;
    @Column(name = "landline_number1")
    private String landlineNumber1;
//    @Column(name = "landline_number2")
//    private String landlineNumber2;
    @Column(name = "email_id")
    private String emailId;
    @Column(name = "email_id1")
    private String emailId1;
//    @Column(name = "email_id2")
//    private String emailId2;
//    @Column(name = "lease_circuit_remarks")
//    private String leaseCircuitRemarks;
//    @Column(name = "trai_rate")
//    private Long traiRate;
    @Column(name = "otc_charges_file")
    private String otcChargesFile;
    @Column(name = "service_charger_file")
    private String serviceChargerFile;
    @Column(name = "static_or_pooled_ip")
    private String staticOrPooledIP;
    @Column(name = "charge_type_file")
    private String chargeTypeFile;
    @Column(name = "billing_cycle")
    private String billingCycle;
    @Column(name = "billing_type")
    private String billingType;
    @Column(name = "billable")
    private String billable;               //CircuitOrAccount;
    @Column(name = "billing_group")
    private String billingGroup;
    @Column(name = "payable")
    private String payable;                 //circuit or account;
    @Column(name = "enable_processing")
    private String enableProcessing;         //- Yes or No
    @Column(name = "deposite")
    private String deposite;
    @Column(name = "po_number")
    private String poNumber;
    @Column(name = "bill_remark")
    private String billRemark;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "organisation")
    private String organisation;
    @Column(name = "address1")
    private String address1;
    @Column(name = "address2")
    private String address2;
    @Column(name = "city")
    private String city;
    @Column(name = "zipcode")
    private String zipcode;
    @Column(name = "state")
    private String state;
    @Column(name = "country")
    private String country;
    @Column(name = "status")
    private String status;
    @Column(columnDefinition = "Boolean default false")
    private Boolean isDelete = false;
    @Column(name = "MVNOID")
    private Integer mvnoId;
    @Column(name = "buid")
    private Long buId;

    @Column(name = "s_discount_type")
    private String discountType="One-time";


    @Column(name = "s_discount")
    private Double discount=0.0;


    @Column(name = "discount_expiry_date")
    private LocalDate discountExpiryDate;


    @Column(name = "service_area_type")
    private String serviceAreaType;

    @Column(name = "new_discount_type")
    private String newDiscountType;


    @Column(name = "new_discount")
    private Double newDiscount;


    @Column(name = "new_discount_expiry_date")
    private LocalDate newDiscountExpiryDate;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @Column(name = "next_staff")
    private Integer nextStaff;

    @Column(name = "branch")
    private String branch;

    @Column(name = "connection_type")
    private String connectionType;

    @Transient
    private String serviceName;

    @Column(name = "hold_service_date", nullable = false, length = 40)
    private LocalDateTime serviceHoldDate;

    @Column(name = "service_hold_by")
    private String serviceHoldBy;

    @Column(name = "service_hold_remarks")
    private String serviceHoldRemarks;

    @Column(name = "service_resume_by")
    private String serviceResumeBy;

    @Column(name = "service_resume_remarks")
    private String serviceResumeRemarks;

    @Column(name = "resume_service_date", nullable = false, length = 40)
    private LocalDateTime serviceResumeDate;

    @Column(name = "stop_service_remarks")
    private String stopServiceRemark;

    @Column (name = "discount_flow_in_process")
    private String discountFlowInProcess;


    public CustomerServiceMapping(CustomerServiceMappingRevenue customerServiceMapping){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


        this.id =customerServiceMapping.getId();
        this.custId =customerServiceMapping.getCustId();
        this.serviceId =customerServiceMapping.getServiceId();
        this.connectionNo =customerServiceMapping.getConnectionNo();
        this.isDeleted =customerServiceMapping.getIsDeleted();
        this.invoiceformat =customerServiceMapping.getInvoiceformat();
        this.invoiceType =customerServiceMapping.getInvoiceType();
        this.cafNo =customerServiceMapping.getCafNo();
        this.uploadCAF =customerServiceMapping.getUploadCAF();
        this.customerName =customerServiceMapping.getCustomerName();
        this.accountNumber =customerServiceMapping.getAccountNumber();
        this.partner =customerServiceMapping.getPartner();
        if (customerServiceMapping.getExpiryDate()!=null) {
            this.expiryDate = LocalDate.parse(customerServiceMapping.getExpiryDate());
        }
        this.chargeTypeFile =customerServiceMapping.getChargeTypeFile();
        this.billingCycle =customerServiceMapping.getBillingCycle();
        this.billingType =customerServiceMapping.getBillingType();
        this.billable =customerServiceMapping.getBillable();
        this.billingGroup =customerServiceMapping.getBillingGroup();
        this.payable =customerServiceMapping.getPayable();
        this.organisation =customerServiceMapping.getOrganisation();
        this.isDelete =customerServiceMapping.getIsDelete();
        this.mvnoId =customerServiceMapping.getMvnoId();
        this.buId =customerServiceMapping.getBuId();
        this.discountType =customerServiceMapping.getDiscountType();
        if(customerServiceMapping.getDiscountExpiryDate()!=null) {
            this.discountExpiryDate = LocalDate.parse(customerServiceMapping.getDiscountExpiryDate());
        }
        this.newDiscountType =customerServiceMapping.getNewDiscountType();
        this.newDiscount =customerServiceMapping.getNewDiscount();
        if (customerServiceMapping.getNewDiscountExpiryDate()!=null){
            this.newDiscountExpiryDate =LocalDate.parse(customerServiceMapping.getNewDiscountExpiryDate());
        }
        this.remarks =customerServiceMapping.getRemarks();
        this.nextTeamHierarchyMappingId =customerServiceMapping.getNextTeamHierarchyMappingId();
        this.nextStaff =customerServiceMapping.getNextStaff();
        this.connectionType =customerServiceMapping.getConnectionType();
        this.serviceName =customerServiceMapping.getServiceName();
        if (customerServiceMapping.getServiceHoldDate()!=null) {
            this.serviceHoldDate = LocalDateTime.parse(customerServiceMapping.getServiceHoldDate(), formatter);
        }
        this.serviceResumeBy =customerServiceMapping.getServiceResumeBy();
        if(customerServiceMapping.getServiceResumeDate()!=null) {
            this.serviceResumeDate = LocalDateTime.parse(customerServiceMapping.getServiceResumeDate(), formatter);
        }
        this.stopServiceRemark =customerServiceMapping.getStopServiceRemark();
        this.discountFlowInProcess =customerServiceMapping.getDiscountFlowInProcess();
    }


}
