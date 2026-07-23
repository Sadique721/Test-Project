package com.savbill.notification.Mvno.model;

import lombok.Data;

@Data
public class MvnoDTO{
 
	private Long id;
	
	private String name;

	private String username;

	private String password;

	private String suffix;
	
	private String description;
	
	private String email;
	
	private String phone;
	
	private String status;
	
	private String logfile;
	
	private String mvnoHeader;
	
	private String mvnoFooter;
	
    private Boolean isDelete = false;
	


}
