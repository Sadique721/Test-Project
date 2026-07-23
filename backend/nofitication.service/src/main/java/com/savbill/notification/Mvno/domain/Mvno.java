package com.savbill.notification.Mvno.domain;


import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString
@Table(name = "tblmmvno")

public class Mvno{
	
	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Long id;
	
	@Column(name = "NAME", nullable = false, length = 64)
    private String name;

	@Column(name = "USERNAME", nullable = false, length = 200)
	private String username;

	@Column(name = "PASSWORD", nullable = false, length = 200)
	private String password;
	
	@Column(name = "SUFFIX", nullable = false, length = 16)
    private String suffix;

	@Column(name = "DESCRIPTION", nullable = false, length = 255)
    private String description;
	
	@Column(name = "EMAIL", nullable = false, length = 255)
    private String email;
	
	@Column(name = "PHONE", nullable = false, length = 255)
    private String phone;
    
	@Column(name = "STATUS", nullable = false, length = 40)
	private String status;
	
	@Column(name = "LOGOFILE", nullable = false, length = 255)
	private String logfile;
	
	@Column(name = "MVNOHEADER", nullable = false, length = 255)
	private String mvnoHeader;
	
	@Column(name = "MVNOFOOTER", nullable = false, length = 255)
	private String mvnoFooter;
	
	@Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;


}
