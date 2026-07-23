package com.savbill.notification.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import com.savbill.notification.helper.SystemConfigDTO;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBLMSYSTEMCONFIG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated config Id", required = true)
    @Column(name = "id", nullable = false)
    private Long id;

    @ApiModelProperty(notes = "Name of the service where config is applied", required = true)
    @Column(name = "servicename", nullable = false, length = 250)
    private String serviceName;

    @ApiModelProperty(notes = "key of config", required = true)
    @Column(name = "configkey", nullable = false, length = 250, unique = true)
    private String key;

    @ApiModelProperty(notes = "value of config", required = true)
    @Column(name = "configvalue", nullable = false, length = 250)
    private String configValue;

    @ApiModelProperty(notes = "This is mvnoid", required = true)
    @Column(name = "mvnoid", nullable = false)
    private Long mvnoId;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH-mm-ss")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH-mm-ss")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "lastmodifieddate", nullable = true, updatable = true)
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(name = "createdby", nullable = false, length = 40, updatable = false)
    private String createdBy;

    @DiffIgnore
    @LastModifiedBy
    @Column(name = "lastmodifiedby", nullable = false, length = 40)
    private String lastModifiedBy;

    public SystemConfig(SystemConfigDTO configDTO, Long mvnoId) {
        this.serviceName = configDTO.getServiceName();
        this.key = configDTO.getKey();
        this.configValue = configDTO.getConfigValue();
        this.mvnoId = mvnoId;
    }

	public SystemConfig(SystemConfig config) {
		if(ValidateCrudTransactionData.validateLongTypeFieldValue(config.getId()))
			this.id = config.getId();
		this.serviceName = config.getServiceName();
		this.key = config.getKey();
		this.configValue = config.getConfigValue();
		this.mvnoId = config.getMvnoId();
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(config.getCreatedBy()))
			this.createdBy = config.getCreatedBy();
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(config.getLastModifiedBy()))
			this.lastModifiedBy = config.getLastModifiedBy();
	}
    
    
}
