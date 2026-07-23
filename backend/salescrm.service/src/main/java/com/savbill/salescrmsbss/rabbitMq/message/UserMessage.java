package com.savbill.salescrmsbss.rabbitMq.message;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {

	private Integer id;

	private String username;

	private String password;

	private String firstname;

	private String lastname;

	private String email;

	private String phone;

	private Integer failcount = 0;

	private String status;
	
	private String countryCode;

	private String last_login_time;

	private String createdate;

	private String updatedate;

	private Integer partnerid;

	private Set<RoleMessage> roles = new HashSet<>();
	
	private Set<BusinessUnitMessage> businessUnitMessageList = new HashSet<>();
	
	private Set<TeamsMessage> teamMessageList = new HashSet<>();

	private String otp;

	private String otpvalidate;

	private Boolean isDelete;

	private Boolean sysstaff;

	private Long serviceareaId;

	private Long businessunitid;

	private Integer staffUserparentId;

	private Integer mvnoId;

	private Integer branchId;
}
