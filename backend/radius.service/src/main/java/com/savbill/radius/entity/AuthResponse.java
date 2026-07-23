package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBLMAUTHRESPONSE")
public class AuthResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated Auth Response Id")
    @Column (name="authresid", nullable = false)
	private Long authresId;
	
	@ApiModelProperty(notes = "This is User Name of Auth Response")
    @Column (name="username", nullable = false , length = 250)
    private String userName;
	
	@ApiModelProperty(notes = "This is reply message of Auth Response")
    @Column (name="replymessage", nullable = false , length = 250)
    private String replyMessage;
	
	@ApiModelProperty(notes = "This is Packet Type of Auth Response")
    @Column (name="packettype", nullable = false , length = 250)
    private String packetType;
	
	@ApiModelProperty(notes = "This is Client IP of Auth Response")
    @Column (name="clientip", nullable = false , length = 250)
    private String clientIp;
	
	@ApiModelProperty(notes = "This is Client Group of Auth Response")
    @Column (name="clientgroup", nullable = false , length = 250)
    private String clientGroup;
	
	@ApiModelProperty(hidden = true)
	@Column (name="eventtime")
	@JsonProperty("eventTime")
	private Timestamp eventTime;
	
	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	@JsonProperty("lastModificationDate")
	private Timestamp lastModifiedOn;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	@Transient
	private String name;


	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public Long getAuthresId() {
		return authresId;
	}

	public void setAuthresId(Long authresId) {
		this.authresId = authresId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getReplyMessage() {
		return replyMessage;
	}

	public void setReplyMessage(String replyMessage) {
		this.replyMessage = replyMessage;
	}

	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getClientGroup() {
		return clientGroup;
	}

	public void setClientGroup(String clientGroup) {
		this.clientGroup = clientGroup;
	}

	public Timestamp getEventTime() {
		return eventTime;
	}

	public void setEventTime(Timestamp eventTime) {
		this.eventTime = eventTime;
	}

	public Timestamp getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(Timestamp lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

