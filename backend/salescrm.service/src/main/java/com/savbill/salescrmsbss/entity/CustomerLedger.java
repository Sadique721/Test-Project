package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.CustomerLedgerPojo;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TBLMCUSTLEDGER")
public class CustomerLedger {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CUSTLEDGERID", nullable = false, length = 40)
	private Integer id;

	private Double totaldue;

	private Double totalpaid;
	
	public CustomerLedger(CustomerLedgerPojo customerLedgerPojo) {
		this.id = customerLedgerPojo.getId();
		this.totaldue = customerLedgerPojo.getTotaldue();
		this.totalpaid = customerLedgerPojo.getTotalpaid();
	}
}
