package com.savbill.integrationsystem.billgen.model;

import com.savbill.integrationsystem.rabbitmq.CustomerMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CustomerDTO {

	private Long id;
	private String username;
	private String firstname;
	private String lastname;
	private String email;
	private String mobile;
	private Integer mvnoId;
	private Long buId;
	private Long servicearea;
	private Long branch;
	private String status;
	private String countryCode;
	private LocalDateTime createdDate;
	private LocalDateTime lastmodifiedDate;
	private String createbyname;
	private String updatebyname;
	private Integer createdByStaffId;
	private Integer lastModifiedByStaffId;
	private String accountNumber;
	private String customerType;
	private Boolean isorgcust = false;
	private String pan;
	private Integer parentcustid;
    private String pop;
    private String olt;

    public CustomerDTO(CustomerMessage customer) {
        this.id = customer.getId();
        this.username = customer.getUsername();
        this.firstname = customer.getFirstname();
        this.lastname = customer.getLastname();
        this.email = customer.getEmail();
        this.mobile = customer.getMobile();
        this.mvnoId = customer.getMvnoId();
        this.buId = customer.getBuId();
        this.servicearea = customer.getServicearea();
        this.branch = customer.getBranch();
        this.status = customer.getStatus();
        this.countryCode = customer.getCountryCode();
        this.createdDate = customer.getCreatedDate();
        this.lastmodifiedDate = customer.getLastmodifiedDate();
        this.createbyname = customer.getCreatebyname();
        this.updatebyname = customer.getUpdatebyname();
        this.createdByStaffId = customer.getCreatedByStaffId();
        this.lastModifiedByStaffId = customer.getLastModifiedByStaffId();
        this.accountNumber = customer.getAccountNumber();
        this.customerType=customer.getCustomerType();
        this.isorgcust=customer.getIsorgcust();
        this.pan=customer.getPan();
        this.parentcustid=customer.getParentcustid();
    }
}
