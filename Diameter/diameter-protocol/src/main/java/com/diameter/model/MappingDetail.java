package com.diameter.model;

import java.time.LocalDateTime;

public class MappingDetail {
	private String id;
	private String mappingHeaderId;
    private String requestAvp;
    private String responseAvp;
    private String valueExpression;
    private ValueType valueType;
    private int sequence;
    private int vendorId;
    private boolean mandatory; 

    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime modifiedDate;
    private String modifiedBy;
    
    public enum ValueType {
        INTEGER("Integer"),
        STRING("String"),
        FLOA("Float"),
        BYTE("Byte"),
        DATE("Date");
        
        private final String dbValue;

        ValueType(String dbValue) {
			this.dbValue = dbValue;
		}

		public String getDbValue() {
			return dbValue;
		}

		public static ValueType fromDbValue(String value) {
			for (ValueType valueType : values()) {
				if (valueType.dbValue.equalsIgnoreCase(value)) {
					return valueType;
				}
			}
			throw new IllegalArgumentException("Invalid status: " + value);
		}
    }
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMappingHeaderId() {
		return mappingHeaderId;
	}

	public void setMappingHeaderId(String mappingHeaderId) {
		this.mappingHeaderId = mappingHeaderId;
	}
	public String getRequestAvp() {
		return requestAvp;
	}
	public void setRequestAvp(String requestAvp) {
		this.requestAvp = requestAvp;
	}
	public String getResponseAvp() {
		return responseAvp;
	}
	public void setResponseAvp(String responseAvp) {
		this.responseAvp = responseAvp;
	}
	public String getValueExpression() {
		return valueExpression;
	}
	public void setValueExpression(String valueExpression) {
		this.valueExpression = valueExpression;
	}
	public ValueType getValueType() {
		return valueType;
	}
	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}
	
	@Override
	public String toString() {
		return "MappingDetail [id=" + id + ", mappingHeaderId=" + mappingHeaderId + ", requestAvp=" + requestAvp
				+ ", responseAvp=" + responseAvp + ", valueExpression=" + valueExpression + ", valueType=" + valueType
				+ ", sequence=" + sequence + ", vendorId=" + vendorId + ", mandatory=" + mandatory + ", createdDate="
				+ createdDate + ", createdBy=" + createdBy + ", modifiedDate=" + modifiedDate + ", modifiedBy="
				+ modifiedBy + "]";
	}
}