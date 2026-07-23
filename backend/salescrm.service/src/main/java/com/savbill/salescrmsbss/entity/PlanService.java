package com.savbill.salescrmsbss.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.savbill.salescrmsbss.audit.Auditable;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "TBLMSERVICES")
public class PlanService extends Auditable<PlanService> {

	/*
	 * CREATE TABLE TBLMSERVICES ( serviceid SERIAL PRIMARY KEY, servicename
	 * varchar(255), CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	 * CREATEDBYSTAFFID NUMERIC(20), LASTMODIFIEDBYSTAFFID NUMERIC(20),
	 * LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP );
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "serviceid", nullable = false, length = 40)
	private Integer id;

	@Column(name = "servicename", nullable = false, length = 40)
	private String name;

	@Column(name = "icname", nullable = false, length = 40)
	private String icname;

	@Column(name = "iccode", nullable = false, length = 40)
	private String iccode;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;

	@Column(name = "BUID", nullable = false, length = 40, updatable = false)
	private Long buId;

	@Column(name = "is_qosv", nullable = false, columnDefinition = "Boolean default true")
	private Boolean isQoSV;

	@Column(name = "expiry", nullable = false, length = 100)
	private String expiry;

	private String ledgerId;

	@Column(name = "is_dtv")
	private Boolean is_dtv;

	@Column(name = "investmentcode_id")
	private Long investmentid;

	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "tbltserviceinventorymapping", joinColumns = @JoinColumn(name = "serviceid", referencedColumnName = "serviceid"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "product_id"))
	private List<ProductCategory> productCategories = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, targetEntity = ServiceParamMapping.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceid")
	List<ServiceParamMapping> serviceParamMappingList;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanService other = (PlanService) obj;
		return Objects.equals(buId, other.buId) && Objects.equals(expiry, other.expiry)
				&& Objects.equals(iccode, other.iccode) && Objects.equals(icname, other.icname)
				&& Objects.equals(id, other.id) && Objects.equals(investmentid, other.investmentid)
				&& Objects.equals(isQoSV, other.isQoSV) && Objects.equals(is_dtv, other.is_dtv)
				&& Objects.equals(ledgerId, other.ledgerId) && Objects.equals(mvnoId, other.mvnoId)
				&& Objects.equals(name, other.name) && Objects.equals(productCategories, other.productCategories)
				&& Objects.equals(serviceParamMappingList, other.serviceParamMappingList);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(buId, expiry, iccode, icname, id, investmentid, isQoSV, is_dtv, ledgerId,
				mvnoId, name, productCategories, serviceParamMappingList);
		return result;
	}

}