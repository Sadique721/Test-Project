package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.ServiceParamMappingDTO;

import lombok.Data;

@Data
@Entity
@Table(name = "tbltserviceparamservicemapping")
public class ServiceParamMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

//    @ManyToMany(targetEntity = PlanService.class)
//    @JoinColumn(name = "serviceid", referencedColumnName = "id", updatable = true, insertable = true)
	@Column(name = "serviceid")
	private Long serviceid;

	@Column(name = "serviceparamid")
	private Long serviceParamId;

	@Column(name = "value")
	private String value;

	@Column(name = "ismandatory")
	private Boolean isMandatory;

	@Column(name = "buid")
	private Integer buId;

	@Column(name = "mvnoid")
	private Integer mvnoId;

	public ServiceParamMapping() {
	}

	public ServiceParamMapping(ServiceParamMappingDTO dto) {
		if (dto.getId() != null)
			this.id = dto.getId();
		this.buId = dto.getBuId();
		this.isMandatory = dto.getIsMandatory();
		if (dto.getMvnoId() != null)
			this.mvnoId = dto.getMvnoId();
		if (dto.getServiceid() != null)
			this.serviceid = dto.getServiceid();
		if (dto.getServiceParamId() != null)
			this.serviceParamId = dto.getServiceParamId();
		this.value = dto.getValue();
	}
}