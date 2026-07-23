package com.savbill.salescrmsbss.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Entity
@Table(name = "TBLMNOTIFICATIONEVENT")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated event id")
	@Column(name = "eventid", nullable = false)
	private Long eventId;

	@ApiModelProperty(notes = "This is event name", required = true)
	@Column(name = "eventname", length = 250, nullable = false)
	private String eventName;

	@ApiModelProperty(notes = "This is event type", allowableValues = "Schedule,Trigger", value = "This field accept value only : Schedule or Trigger", required = true)
	@Column(name = "eventtype", length = 20, nullable = false)
	private String eventType;

	@ApiModelProperty(notes = "This is event description", required = false)
	@Column(name = "description", length = 700, nullable = true)
	private String description;

	@ApiModelProperty(notes = "Status of the template", allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive", required = true)
	@Column(name = "status", nullable = false, length = 250)
	private String status;

	@ApiModelProperty(hidden = true)
	@Column(name = "createdate")
	private Timestamp createDate;

	@ApiModelProperty(hidden = true)
	@Column(name = "lastmodificationdate")
	private Timestamp lastModificationDate;

	@ApiModelProperty(notes = "This is mvno id", required = true)
	@Column(name = "mvnoid", nullable = false)
	private Long mvnoId;

	public Event() {
	}

	public Event(Long id) {
		this.eventId = id;
	}

}
