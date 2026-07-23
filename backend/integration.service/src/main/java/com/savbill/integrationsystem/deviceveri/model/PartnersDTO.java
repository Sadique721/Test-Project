package com.savbill.integrationsystem.deviceveri.model;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class PartnersDTO extends Auditable<Long> implements IBaseDto{
	private Long id;
	private String partnername;
	private String status;
	private LocalDateTime createdate;
	private Integer createdbystaffid;
	private Integer lastmodifiedbystaffid;
	private LocalDateTime lastmodifieddate;
	private String commType;
	private Double commRelValue;
	private Double commDueDay;
	private LocalDateTime nextbilldate;
	private LocalDateTime lastbilldate;
	private Long taxid;
	private String addresstype;
	private String address1;
	private String address2;
	private Double city;
	private Double state;
	private Long country;
	private String pincode;
	private String mobile;
	private String email;
	private Boolean isDelete;
	private Integer parentpartnerid;
	private Long pricebookid;
	private String createbyname;
	private String updatebyname;
	private Long mvnoid;
	private String commissionShareType;
	private Double balance;
	private String countryCode;
	private Long buid;
	private Long newCustomerCount;
	private Long renewCustomerCount;
	private Long totalCustomerCount;
	private Double credit;
	private String calendartype;
	private LocalDateTime resetDate;
	private String partnerCode;
	private String partnerType;
	private String contactPersonName;
	private String companyName;
	private String panDetails;
	private Double creditConsume;
	private Long region;
	private Long branch;
	private Long bussinessVertical;
	private Boolean isDunningEnable;
	private String dunningAction;
	private String dunningActivateFor;
	private LocalDateTime lastDunningDate;
	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public Long getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMvnoId(Long mvnoId) {
		// TODO Auto-generated method stub
		
	}
}
