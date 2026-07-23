package com.savbill.salescrmsbss.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.audit.Auditable;

import lombok.Data;

@Data
@Entity
@Table(name = "tbltfieldserviceparamrel")
public class FieldServiceParamMapping extends Auditable<FieldServiceParamMapping>{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(targetEntity = Fields.class)
	@JoinColumn(name = "fieldid", referencedColumnName = "id", updatable = true, insertable = true)
	private Fields fields;

	@ManyToOne(targetEntity = ServiceParameter.class)
	@JoinColumn(name = "serviceparamid", referencedColumnName = "id", updatable = true, insertable = true)
	private ServiceParameter serviceParameter;

	@Column(name = "is_mandatory")
	private Boolean is_mandatory;

	@Column(name = "module")
	private String module;

	@Column(name = "is_deleted")
	private Boolean is_deleted;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldServiceParamMapping other = (FieldServiceParamMapping) obj;
		return Objects.equals(fields, other.fields) && Objects.equals(id, other.id)
				&& Objects.equals(is_deleted, other.is_deleted) && Objects.equals(is_mandatory, other.is_mandatory)
				&& Objects.equals(module, other.module) && Objects.equals(serviceParameter, other.serviceParameter);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(fields, id, is_deleted, is_mandatory, module, serviceParameter);
		return result;
	}
}
