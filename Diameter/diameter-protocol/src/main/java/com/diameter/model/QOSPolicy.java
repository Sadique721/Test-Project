package com.diameter.model;

import com.diameter.kafka.CustomMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "tbl_qos_policy")
public class QOSPolicy {

	@Id
	@Column(name = "id", nullable = false)
	private BigInteger id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "thpolicyname")
	private String thPolicyName;

	@Column(name = "baseparam1")
	private String baseParam1;

	@Column(name = "baseparam2")
	private String baseParam2;

	@Column(name = "baseparam3")
	private String baseParam3;

	@Column(name = "thparam1")
	private String thParam1;

	@Column(name = "thparam2")
	private String thParam2;

	@Column(name = "thparam3")
	private String thParam3;

	@Column(name = "is_deleted")
	private Short isDeleted;

	@Column(name = "createdbystaffid")
	private BigDecimal createdByStaffid;

	@Column(name = "createdate")
	private Timestamp createDate;

	@Column(name = "lastmodifiedbystaffid")
	private BigDecimal lastModifiedByStaffId;

	@Column(name = "lastmodifieddate")
	private Timestamp lastModifiedDate;

	@Column(name = "basepolicyname")
	private String basePolicyName;

	@Column(name = "createbyname")
	private String createByName;

	@Column(name = "updatebyname")
	private String updateByName;

	@Column(name = "MVNOID")
	private BigInteger MVNOID;

	@Column(name = "type")
	private String type;

	@Column(name = "qosspeed")
	private String qosSpeed;

	@Column(name = "upstreamprofileuid")
	private String upstreamProfileUID;

	@Column(name = "downstreamprofileuid")
	private String downstreamProfileUID;

	@OneToMany(
			mappedBy = "qosPolicy",
			cascade = CascadeType.ALL,
			orphanRemoval = true
	)
	private List<QOSPolicyGatewayMappingEntity> qosPolicyGatewayMappingList = new ArrayList<>();

	public void addGatewayMapping(QOSPolicyGatewayMappingEntity mapping) {
		qosPolicyGatewayMappingList.add(mapping);
		mapping.setQosPolicy(this);
	}

	@Override
	public String toString() {
		return "QOSPolicy [id=" + id + ", name=" + name + ", description=" + description
				+ ", thPolicyName=" + thPolicyName + ", baseParam1=" + baseParam1
				+ ", baseParam2=" + baseParam2 + ", baseParam3=" + baseParam3
				+ ", thParam1=" + thParam1 + ", thParam2=" + thParam2
				+ ", thParam3=" + thParam3 + ", isDeleted=" + isDeleted
				+ ", createdByStaffid=" + createdByStaffid + ", createDate=" + createDate
				+ ", lastModifiedByStaffId=" + lastModifiedByStaffId
				+ ", lastModifiedDate=" + lastModifiedDate
				+ ", basePolicyName=" + basePolicyName
				+ ", createByName=" + createByName
				+ ", updateByName=" + updateByName
				+ ", MVNOID=" + MVNOID
				+ ", type=" + type
				+ ", qosSpeed=" + qosSpeed
				+ ", upstreamProfileUID=" + upstreamProfileUID
				+ ", downstreamProfileUID=" + downstreamProfileUID + "]";
	}

	public QOSPolicy() {}

	public QOSPolicy(CustomMessage customMessage) {
		Map<String, Object> message = customMessage.getData();
		if (message.get("id") != null) {
			this.id = new BigInteger(message.get("id").toString());
		}
		if (message.get("name") != null) {
			this.name = message.get("name").toString();
		}
		if (message.get("description") != null) {
			this.description = message.get("description").toString();
		}
		if (message.get("basepolicyname") != null) {
			this.basePolicyName = message.get("basepolicyname").toString();
		}
		if (message.get("thpolicyname") != null) {
			this.thPolicyName = message.get("thpolicyname").toString();
		}
		if (message.get("baseparam1") != null) {
			this.baseParam1 = message.get("baseparam1").toString();
		}
		if (message.get("baseparam2") != null) {
			this.baseParam2 = message.get("baseparam2").toString();
		}
		if (message.get("baseparam3") != null) {
			this.baseParam3 = message.get("baseparam3").toString();
		}
		if (message.get("thparam1") != null) {
			this.thParam1 = message.get("thparam1").toString();
		}
		if (message.get("thparam2") != null) {
			this.thParam2 = message.get("thparam2").toString();
		}
		if (message.get("thparam3") != null) {
			this.thParam3 = message.get("thparam3").toString();
		}
		if (message.get("isDeleted") != null) {
			this.isDeleted = Short.valueOf(
					Boolean.parseBoolean(message.get("isDeleted").toString()) ? "1" : "0"
			);
		}
		if (message.get("mvnoId") != null) {
			this.MVNOID = new BigInteger(message.get("mvnoId").toString());
		}
		if(message.get("type") != null){
			this.type = message.get("type").toString();
		}
		if(message.get("qosspeed") != null){
			this.qosSpeed = message.get("qosspeed").toString();
		}
		if(message.get("upstreamprofileuid") != null){
			this.upstreamProfileUID = message.get("upstreamprofileuid").toString();
		}
		if(message.get("downstreamprofileuid") != null){
			this.downstreamProfileUID = message.get("downstreamprofileuid").toString();
		}
		if (message.get("qosPolicyGatewayMappingList") != null) {

			List<LinkedHashMap<String, Object>> mappings =
					(List<LinkedHashMap<String, Object>>)
							message.get("qosPolicyGatewayMappingList");

			for (LinkedHashMap<String, Object> map : mappings) {

				QOSPolicyGatewayMappingEntity entity =
						new QOSPolicyGatewayMappingEntity();

				// ✅ ID FROM KAFKA
				entity.setId(Long.valueOf(map.get("id").toString()));

				entity.setGatewayName(map.get("gatewayName").toString());
				entity.setDownloadSpeed(map.get("downloadSpeed").toString());
				entity.setUploadSpeed(map.get("uploadSpeed").toString());
				entity.setBaseDownloadSpeed(map.get("baseDownloadSpeed").toString());
				entity.setBaseUploadSpeed(map.get("baseUploadSpeed").toString());
				entity.setThrottleDownloadSpeed(map.get("throttleDownloadSpeed").toString());
				entity.setThrottleUploadSpeed(map.get("throttleUploadSpeed").toString());

				// ❌ DO NOT SET qosPolicyId manually

				this.addGatewayMapping(entity);
			}
		}

	}

}
