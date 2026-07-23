package com.savbill.integrationsystem.deviceveri.model;

import com.savbill.integrationsystem.core.dto.IBaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class CustomersDTO implements IBaseDto {
    private Long custid;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private Long serviceareaId;
    private Long branchid;
    private String status;
    private String countryCode;
    private Long build;
    private Long mvnoId;
//    private LocalDateTime createDate;
//    private LocalDateTime lastModifiedDate;
//    private String createbyname;
//    private String updatebyname;
//    private Long createdStaffId;
//    private Long LastModifiedStaffId;
    private String accountNumber;
    private String customerType;
    private Integer isorgcust;
    private String pan;
    private Integer parentcustid;
    private Long partnerId;

	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return custid;
	}
	
}
