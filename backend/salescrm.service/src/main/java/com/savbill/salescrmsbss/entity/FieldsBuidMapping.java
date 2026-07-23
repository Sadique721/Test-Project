package com.savbill.salescrmsbss.entity;

import java.util.Objects;

import javax.persistence.*;

import com.savbill.salescrmsbss.audit.Auditable;

@Entity
//@EntityListeners(AuditableListener.class)
@Table(name = "tbltfieldsbuidmapping")
//@Data
public class FieldsBuidMapping extends Auditable<FieldsBuidMapping>{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "field_id")
	private Long fieldId;

	@Column(name = "buid")
	private Long buid;

	@Column(name = "is_mandatory")
	private Boolean isMandatory;
	@Column(name = "screen")
	private Long screen;
	@Column(name = "module")
	private String module;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "data_type")
	private String dataType;

	@Column(name = "field_name")
	private String fieldName;

	@Column(name = "serviceparamid")
	private Long serviceParamId;

	@Column(name = "default_mandatory")
	private Boolean defaultMandatory;

	public FieldsBuidMapping() {
	}
	public FieldsBuidMapping(FieldsBuidMapping fieldsBuidMapping) {
		this.fieldId = fieldsBuidMapping.getFieldId();
		this.isMandatory = fieldsBuidMapping.getIsMandatory();
		this.screen = fieldsBuidMapping.getScreen();
		this.module = fieldsBuidMapping.getModule();
		this.dataType = fieldsBuidMapping.getDataType();
		this.fieldName = fieldsBuidMapping.getFieldName();
		this.defaultMandatory = fieldsBuidMapping.getDefaultMandatory();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldsBuidMapping other = (FieldsBuidMapping) obj;
		return Objects.equals(buid, other.buid) && Objects.equals(dataType, other.dataType)
				&& Objects.equals(defaultMandatory, other.defaultMandatory) && Objects.equals(fieldId, other.fieldId)
				&& Objects.equals(fieldName, other.fieldName) && Objects.equals(id, other.id)
				&& Objects.equals(isDeleted, other.isDeleted) && Objects.equals(isMandatory, other.isMandatory)
				&& Objects.equals(module, other.module) && Objects.equals(screen, other.screen)
				&& Objects.equals(serviceParamId, other.serviceParamId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(buid, dataType, defaultMandatory, fieldId, fieldName, id, isDeleted,
				isMandatory, module, screen, serviceParamId);
		return result;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	public Long getBuid() {
		return buid;
	}
	public void setBuid(Long buid) {
		this.buid = buid;
	}
	public Boolean getIsMandatory() {
		return isMandatory;
	}
	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	public Long getScreen() {
		return screen;
	}
	public void setScreen(Long screen) {
		this.screen = screen;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Long getServiceParamId() {
		return serviceParamId;
	}
	public void setServiceParamId(Long serviceParamId) {
		this.serviceParamId = serviceParamId;
	}
	public Boolean getDefaultMandatory() {
		return defaultMandatory;
	}
	public void setDefaultMandatory(Boolean defaultMandatory) {
		this.defaultMandatory = defaultMandatory;
	}
}
