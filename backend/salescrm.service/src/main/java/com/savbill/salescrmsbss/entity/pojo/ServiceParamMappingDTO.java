package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.ServiceParamMapping;

import lombok.Data;

@Data
public class ServiceParamMappingDTO {

	private Long id;
	private Long serviceid;
	private Long serviceParamId;
//    private ServiceParameter serviceParameter;
	private String value;
	private Boolean isMandatory;
	private Integer buId;
	private Integer mvnoId;

	public ServiceParamMappingDTO() {
	}

	public ServiceParamMappingDTO(ServiceParamMapping instance) {

		if (instance.getId() != null)
			this.id = instance.getId();
		if (instance.getServiceid() != null)
			this.serviceid = instance.getServiceid();
		if (instance.getServiceParamId() != null)
			this.serviceParamId = instance.getServiceParamId();
		this.value = instance.getValue();
		this.isMandatory = instance.getIsMandatory();
		if (instance.getBuId() != null)
			this.buId = instance.getBuId();
		if (instance.getMvnoId() != null)
			this.mvnoId = instance.getMvnoId();
	}
}