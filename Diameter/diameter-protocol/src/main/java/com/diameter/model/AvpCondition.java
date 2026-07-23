package com.diameter.model;

import java.time.LocalDateTime;

public class AvpCondition {
	
	private String id;
	private String mappingHeaderId;
    private int avpCode;
    private int vendorId = 0;
    private int sequence;
    private String expectedValue;
    private String matchType; 
    // EXISTS / EQUALS / CONTAINS
    
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime modifiedDate;
    private String modifiedBy;

    public int getAvpCode() {
        return avpCode;
    }

    public void setAvpCode(int avpCode) {
        this.avpCode = avpCode;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
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
	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AvpCondition [id=");
		builder.append(id);
		builder.append(", mappingHeaderId=");
		builder.append(mappingHeaderId);
		builder.append(", avpCode=");
		builder.append(avpCode);
		builder.append(", vendorId=");
		builder.append(vendorId);
		builder.append(", sequence=");
		builder.append(sequence);
		builder.append(", expectedValue=");
		builder.append(expectedValue);
		builder.append(", matchType=");
		builder.append(matchType);
		builder.append(", createdDate=");
		builder.append(createdDate);
		builder.append(", createdBy=");
		builder.append(createdBy);
		builder.append(", modifiedDate=");
		builder.append(modifiedDate);
		builder.append(", modifiedBy=");
		builder.append(modifiedBy);
		builder.append("]");
		return builder.toString();
	}
}
