package com.savbill.notification.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMNOTIFICATIONCONFIG")
public class NotificationConfig extends Auditable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated Notification config Id")
    @Column (name="notificationconfigid", nullable = false)
	private Long notificationconfigId;
	
	@ApiModelProperty(notes = "This is Business Unit",required = true)
    @Column (name="buname", length = 100, nullable = false)
    private String buName;

	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

	@ApiModelProperty(notes = "this is bu id" , required = false)
	@Column(name = "BUID", length = 40, updatable = false)
	private Long buId;
}	
