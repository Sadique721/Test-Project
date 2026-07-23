package com.savbill.notification.entity;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMSMSCONFIG")
public class SmsConfig extends Auditable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated SMS config Id")
    @Column (name="smsconfigid", nullable = false)
	private Long smsConfigId;
	
	@ApiModelProperty(notes = "This is SMS URL",required = true)
    @Column (name="smsurl", length = 100, nullable = false)
    private String smsUrl;

	@DiffIgnore
	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

	@DiffIgnore
	@ApiModelProperty(notes = "this is bu id" , required = false)
	@Column(name = "BUID", length = 40, updatable = false)
	private Long buId;

	@ApiModelProperty(notes = "please add status", required = true)
	@Column (name="config_status", nullable = false )
	private Boolean configStatus;

	@ApiModelProperty(notes = "This is service type",allowableValues = "BSS,IWF",  value = "This field accept value only : BSS or IWF",required = true)
	@Column (name="service_type", length = 10, nullable = false)
	private String serviceType;

	@Transient
	private String mvnoName;
}	
