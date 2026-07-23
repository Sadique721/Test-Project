package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.CustMacMapppingPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblcustmacmapping")
public class CustMacMappping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "custmacmapid", nullable = false, length = 40)
	private Integer id;

	private String macAddress;

	private Boolean isDeleted;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
	
	public CustMacMappping(CustMacMapppingPojo custMacMapppingPojo) {
		this.id = custMacMapppingPojo.getId();
		this.macAddress = custMacMapppingPojo.getMacAddress();
		this.isDeleted = custMacMapppingPojo.getIsDeleted();
		if(custMacMapppingPojo.getLeadMasterId() != null)
			this.leadMaster = new LeadMaster(custMacMapppingPojo.getLeadMasterId());
	}
}
