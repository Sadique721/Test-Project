package com.savbill.notification.entity;

import com.savbill.notification.helper.NotificationConfigMappingDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMNOTIFICATIONCONFIGMAPPING")
public class NotificationConfigMapping
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated Notification config Mapping Id")
    @Column (name="notificationconfigmappingid", nullable = false)
	private Long notificationConfigMappingId;

	@ApiModelProperty(notes = "This is SMS Config id")
    @Column (name="notificationconfigid", nullable = false , length = 250)
    private Long notificationconfigId;

	@ApiModelProperty(notes = "Parameter of SMS Config")
    @Column (name="parameter", nullable = false , length = 100)
    private String parameter;

	@ApiModelProperty(notes = "Parameter Value of SMS Config")
    @Column (name="value", nullable = false , length = 100)
    private String value;

	@ApiModelProperty(hidden = true)
    @Column (name="createdon")
    private Timestamp createdOn;

	@DiffIgnore
	@ApiModelProperty(hidden = true)
    @Column (name="lastmodifiedon")
    private Timestamp lastModifiedOn;

	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;


	public NotificationConfigMapping(NotificationConfigMappingDto notificationConfigMappingDto, Long mvnoId)
	{
		this.notificationconfigId = notificationConfigMappingDto.getNotificationConfigId();
		this.parameter = notificationConfigMappingDto.getParameter();
		this.value = notificationConfigMappingDto.getValue();
		this.mvnoId = mvnoId;
	}
}
