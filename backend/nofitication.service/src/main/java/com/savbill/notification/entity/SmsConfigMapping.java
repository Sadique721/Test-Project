package com.savbill.notification.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.notification.helper.SmsConfigMappingDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMSMSCONFIGMAPPING")
public class SmsConfigMapping 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated SMS config Mapping Id")
    @Column (name="smsconfigmappingid", nullable = false)
	private Long smsConfigMappingId;
	
	@ApiModelProperty(notes = "This is SMS Config id")
    @Column (name="smsconfigid", nullable = false , length = 250)
    private Long smsConfigId;
	
	@ApiModelProperty(notes = "Parameter of SMS Config")
    @Column (name="parameter", nullable = false , length = 100)
    private String parameter;
	
	@ApiModelProperty(notes = "Parameter Value of SMS Config")
    @Column (name="value", nullable = false , length = 500)
    private String value;
	
	@ApiModelProperty(hidden = true)
    @Column (name="createdon")
    private Timestamp createdOn;
	
	@ApiModelProperty(hidden = true)
    @Column (name="lastmodifiedon")
    private Timestamp lastModifiedOn;
	
	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;


	public SmsConfigMapping(SmsConfigMappingDto smsConfigMappingDto, Long mvnoId) 
	{
		this.smsConfigId = smsConfigMappingDto.getSmsConfigId();
		this.parameter = smsConfigMappingDto.getParameter();
		this.value = smsConfigMappingDto.getValue();
		this.mvnoId = mvnoId;
	}
}
