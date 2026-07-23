package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.CustomerMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblcustomers")
public class Customers {

	@Id
	@Column(name = "custid", nullable = false, length = 40)
	private Integer id;

	@Column(name = "title")
	private String title;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;
	
    @Column(name = "cstatus")
    private String status;
	@Column(name = "MVNOID")
	private Integer mvnoId;
	@Column(name = "BUID")
	private Integer buId;
    @Column(name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
    public Customers(CustomerMessage message) {
    	this.id = message.getId();
    	this.title = message.getTitle();
    	this.username = message.getUsername();
    	this.password = message.getPassword();
    	this.firstname = message.getFirstname();
    	this.lastname = message.getLastname();
    	this.status = message.getStatus();
    	this.isDeleted = message.getIsDeleted();
		this.buId=message.getBuId();
		this.mvnoId= message.getMvnoId();
    }
    
    public Customers(Integer id) {
    	this.id = id;
    }

	public Customers(Integer id, String firstname) {
		this.id = id;
		this.firstname = firstname;
	}

}
