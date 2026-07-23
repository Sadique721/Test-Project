package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.MvnoMessage;

import com.savbill.salescrmsbss.rabbitMq.message.SaveMvnoSharedDataMessage;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateMvnoSharedDataMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmmvno")
public class Mvno {

	@Id
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
	
	public Mvno(MvnoMessage message) {
		this.id = message.getId();
		this.name = message.getName();
		this.username = message.getUsername();
		this.password = message.getPassword();
		this.suffix = message.getSuffix();
		this.description = message.getDescription();
		this.email = message.getEmail();
		this.phone = message.getPhone();
		this.status = message.getStatus();
		this.logfile = message.getLogfile();
		this.mvnoHeader = message.getMvnoHeader();
		this.mvnoFooter = message.getMvnoFooter();
		this.isDelete = message.getIsDelete();
	}

    public Mvno(SaveMvnoSharedDataMessage message) {
		this.id = message.getId();
		this.name = message.getName();
		this.username = message.getUsername();
		this.password = message.getPassword();
		this.suffix = message.getSuffix();
		this.description = message.getDescription();
		this.email = message.getEmail();
		this.phone = message.getPhone();
		this.status = message.getStatus();
		this.logfile = message.getLogfile();
		this.mvnoHeader = message.getMvnoHeader();
		this.mvnoFooter = message.getMvnoFooter();
		this.isDelete = message.getIsDelete();
    }

	public Mvno(UpdateMvnoSharedDataMessage message) {
		this.id = message.getId();
		this.name = message.getName();
		this.username = message.getUsername();
		this.password = message.getPassword();
		this.suffix = message.getSuffix();
		this.description = message.getDescription();
		this.email = message.getEmail();
		this.phone = message.getPhone();
		this.status = message.getStatus();
		this.logfile = message.getLogfile();
		this.mvnoHeader = message.getMvnoHeader();
		this.mvnoFooter = message.getMvnoFooter();
		this.isDelete = message.getIsDelete();
	}
}
