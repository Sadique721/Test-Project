package com.diameter.kafka;

import com.diameter.model.CustomerServiceMapping;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerServiceMappingRevenue {



    private Integer id;

    private Integer custId;

    private Long serviceId;


    private String connectionNo;

    private Boolean isDeleted = false;

    private String invoiceformat;

    private String invoiceType;

    private Long cafNo;

    private String uploadCAF;

    private String customerName;

    private Long accountNumber;


    private Long partner;

    private String expiryDate;

    private String terminationAddress;

    private String chargeTypeFile;

    private String billingCycle;
    private String billingType;

    private String billable;

    private String billingGroup;

    private String payable;


    private String fullName;

    private String organisation;

    private Boolean isDelete = false;

    private Integer mvnoId;

    private Long buId;

    private String discountType="One-time";

    private String discountExpiryDate;

    private String newDiscountType;

    private Double newDiscount;

    private String newDiscountExpiryDate;

    private String remarks;

    private Integer nextTeamHierarchyMappingId;

    private Integer nextStaff;

    private String connectionType;

    private String serviceName;

    private String serviceHoldDate;

    private String serviceHoldBy;

    private String serviceResumeBy;

    private String serviceResumeDate;

    private String stopServiceRemark;

    private String discountFlowInProcess;

    private Double old_discount;

    private String status;

    private String msisdn;

    private String imsi;

    private String username;

    private String password;

    private Integer createdByStaffId;

    private Integer lastModifiedByStaffId;

    private String createdByName;

    private String updatedByName;

    public CustomerServiceMappingRevenue(CustomerServiceMapping customerServiceMapping){
        this.id =customerServiceMapping.getId();
        this.custId =customerServiceMapping.getCustId();
        this.serviceId =customerServiceMapping.getServiceId();
    }

}

