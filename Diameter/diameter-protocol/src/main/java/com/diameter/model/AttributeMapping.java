package com.diameter.model;

public class AttributeMapping {
    private String id;
    private String mappings;
    private String packetMapId;
    private String pccRuleMapId;
    private int orderNumber;
    private String mappingType;

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getMappings() {
		return mappings;
	}
	
	public void setMappings(String mappings) {
		this.mappings = mappings;
	}
	
	public String getPacketMapId() {
		return packetMapId;
	}
	
	public void setPacketMapId(String packetMapId) {
		this.packetMapId = packetMapId;
	}
	
	public String getPccRuleMapId() {
		return pccRuleMapId;
	}
	
	public void setPccRuleMapId(String pccRuleMapId) {
		this.pccRuleMapId = pccRuleMapId;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getMappingType() {
		return mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

	@Override
	public String toString() {
		return "AttributeMapping [id=" + id + ", mappings=" + mappings + ", packetMapId=" + packetMapId
				+ ", pccRuleMapId=" + pccRuleMapId + ", orderNumber=" + orderNumber + ", mappingType=" + mappingType
				+ "]";
	}

}

