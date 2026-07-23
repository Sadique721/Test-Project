package com.savbill.notification.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.*;

import com.savbill.notification.helper.EventDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMEVENT")
@ApiModel(value = "Event Entity",description = "This is Event entity which is used to add event data")
public class Event
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated event id")
    @Column (name="eventid", nullable = false)
	private Long eventId;
	
	@ApiModelProperty(notes = "This is event name",required = true)
    @Column (name="eventname", length = 250, nullable = false)
    private String eventName;
	
	@ApiModelProperty(notes = "This is event type",allowableValues = "Schedule,Trigger",  value = "This field accept value only : Schedule or Trigger",required = true)
    @Column (name="eventtype", length = 20, nullable = false)
    private String eventType;
	
	@ApiModelProperty(notes = "This is event description",required = false)
    @Column (name="description", length = 700, nullable = true)
    private String description;
	 
	@ApiModelProperty(notes = "Status of the template",allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",required = true)
    @Column (name="status", nullable = false , length = 250)
    private String status;
	
	@ApiModelProperty(hidden = true)
	@Column (name="createdate")
	private Timestamp createDate;

	@DiffIgnore
	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	private Timestamp lastModificationDate;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Template> template;
	
	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

	@Column(name="is_active")
	private Boolean isActive = true;

	@Column(name="is_delete")
	private Boolean isDelete = false;

	@Column(name = "email_subject")
	private String emailSubject;

	@Column(name = "to_email_id")
	private String toEmailId;

	@Column(name = "cc_email_id")
	private String ccEmailId;

	@Column(name = "bcc_email_id")
	private String bccEmailId;

	@Column(name = "emailconfigid")
	private Long emailConfigId;

	@Column(name = "timeinterval")
	private String timeInterval;

	@Column(name = "timeintervaltype")
	private String timeIntervalType;

	@Column(name = "convertedtime")
	private Long convertedTime;

	@Column(name = "constraint_type")
	private String constraintType;

	@Column(name = "column_value")
	private String columnValue;

	@Column(name = "regex")
	private String regex;

	@Column(name = "regex_group_index")
	private String regexGroupIndex;

	@Column(name = "system_generated")
	private Boolean systemGenerated = false;

	@Column(name = "servicetype")
	private String serviceType;

	@Column(name = "isfrequency")
	private Boolean isFrequency = false;

//	@JsonManagedReference
//	@OneToOne(mappedBy = "event", fetch = FetchType.EAGER,
//            cascade = CascadeType.ALL) 
//    private EmailTemplate emailTemplate;

	public Event(EventDto eventDto) 
	{
		this.eventName = eventDto.getEventName();
		this.eventType = eventDto.getEventType();
		this.description = eventDto.getDescription();
		this.status=eventDto.getStatus();
		//this.mvnoId=mvnoId;
	}
}
