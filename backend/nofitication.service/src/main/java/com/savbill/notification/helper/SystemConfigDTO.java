package com.savbill.notification.helper;

import com.savbill.notification.entity.SystemConfig;
import com.savbill.notification.utils.ValidateCrudTransactionData;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SystemConfig",description = "This is data transfer object for system config which is used system config crud operation")
public class SystemConfigDTO {

    @ApiModelProperty(notes = "Id of the system config",required=false)
    private Long id;

    @ApiModelProperty(notes = "Service name system config",required=true)
    private String serviceName;

    @ApiModelProperty(notes = "Key of the system config",required=true)
    private String key;

    @ApiModelProperty(notes = "Value of the system config",required=true)
    private String configValue;

    @ApiModelProperty(notes = "MvnoId of the system config",required=true)
    private Long mvnoId;

//    @ApiModelProperty(notes = "Created date and time of the system config",required=false)
//    private LocalDateTime createDate;

//    @ApiModelProperty(notes = "last modified date and time of the system config",required=false)
//    private LocalDateTime lastModifiedDate;

    @ApiModelProperty(notes = "create by user of the system config",required=false)
    private String createdBy;

    @ApiModelProperty(notes = "updated by user of the system config",required=false)
    private String lastModifiedBy;

	public SystemConfigDTO(SystemConfig config) {
		if(ValidateCrudTransactionData.validateLongTypeFieldValue(config.getId()))
			this.id = config.getId();
		this.serviceName = config.getServiceName();
		this.key = config.getKey();
		this.configValue = config.getConfigValue();
		this.mvnoId = config.getMvnoId();
//		this.lastModifiedDate = config.getLastModifiedDate();
		this.lastModifiedBy = config.getLastModifiedBy();
	}
    
    
}
