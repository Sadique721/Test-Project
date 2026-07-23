package com.savbill.integrationsystem.deviceveri.domain;

import com.savbill.integrationsystem.core.data.IBaseData;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblpartners")
public class PartnersData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name="PARTNERID")
	private Long id;
	@Column (name="PARTNERNAME")
	private String partnername;
	@Column (name="status")
	private String status;
	@Column (name="CREATEDATE")
	private LocalDateTime createdate;
	@Column (name="CREATEDBYSTAFFID")
	private Integer createdbystaffid;
	@Column (name="LASTMODIFIEDBYSTAFFID")
	private Integer lastmodifiedbystaffid;
	@Column (name="LASTMODIFIEDDATE")
	private LocalDateTime lastmodifieddate;
	@Column (name="COMM_TYPE")
	private String commType;
	@Column (name="COMM_REL_VALUE")
	private Double commRelValue;
	@Column (name="COMM_DUE_DAY")
	private Double commDueDay;
	@Column (name="NEXTBILLDATE")
	private LocalDateTime nextbilldate;
	@Column (name="LASTBILLDATE")
	private LocalDateTime lastbilldate;
	@Column (name="taxid")
	private Long taxid;
	@Column (name="addresstype")
	private String addresstype;
	@Column (name="address1")
	private String address1;
	@Column (name="address2")
	private String address2;
	@Column (name="city")
	private Double city;
	@Column (name="state")
	private Double state;
	@Column (name="country")
	private Long country;
	@Column (name="pincode")
	private String pincode;
	@Column (name="mobile")
	private String mobile;
	@Column (name="email")
	private String email;
	@Column (name="is_delete")
	private Boolean isDelete;
	@Column (name="parentpartnerid")
	private Integer parentpartnerid;
	@Column (name="pricebookid")
	private Long pricebookid;
	@Column (name="createbyname")
	private String createbyname;
	@Column (name="updatebyname")
	private String updatebyname;
	@Column (name="MVNOID")
	private Long mvnoid;
	@Column (name="commission_share_type")
	private String commissionShareType;
	@Column (name="balance")
	private Double balance;
	@Column (name="country_code")
	private String countryCode;
	@Column (name="BUID")
	private Long buid;
	@Column (name="new_customer_count")
	private Long newCustomerCount;
	@Column (name="renew_customer_count")
	private Long renewCustomerCount;
	@Column (name="total_customer_count")
	private Long totalCustomerCount;
	@Column (name="credit")
	private Double credit;
	@Column (name="calendartype")
	private String calendartype;
	@Column (name="reset_date")
	private LocalDateTime resetDate;
	@Column (name="partner_code")
	private String partnerCode;
	@Column (name="partner_type")
	private String partnerType;
	@Column (name="contact_person_name")
	private String contactPersonName;
	@Column (name="company_name")
	private String companyName;
	@Column (name="pan_details")
	private String panDetails;
	@Column (name="credit_consume")
	private Double creditConsume;
	@Column (name="region")
	private Long region;
	@Column (name="branch")
	private Long branch;
	@Column (name="bussiness_vertical")
	private Long bussinessVertical;
	@Column (name="is_dunning_enable")
	private Boolean isDunningEnable;
	@Column (name="dunning_action")
	private String dunningAction;
	@Column (name="dunning_activate_for")
	private String dunningActivateFor;
	@Column (name="last_dunning_date")
	private LocalDateTime lastDunningDate;

	@Override
	public Long getPrimaryKey() {
		return id;
	}

	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDeleteFlag() {
		// TODO Auto-generated method stub
		return false;
	}
}
