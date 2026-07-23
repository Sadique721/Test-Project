package com.savbill.salescrmsbss.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.audit.Auditable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmserviceparams")
//@EntityListeners(AuditableListener.class)
public class ServiceParameter extends Auditable<ServiceParameter>{

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "name", length = 40)
	private String name;

	@Column(name = "isdelete")
	private Boolean isdelete;

	@Column(name = "field_name")
	private String fieldName;
	@Column(name = "data_type")
	private String dataType;
	private Integer buId;
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceParameter other = (ServiceParameter) obj;
		return Objects.equals(buId, other.buId) && Objects.equals(dataType, other.dataType)
				&& Objects.equals(fieldName, other.fieldName) && Objects.equals(id, other.id)
				&& Objects.equals(isdelete, other.isdelete) && Objects.equals(name, other.name);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(buId, dataType, fieldName, id, isdelete, name);
		return result;
	}
	
}
