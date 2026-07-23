package com.savbill.radius.entity;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.savbill.radius.helper.EventDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMEVENT")
@ApiModel(value = "Event Entity",description = "This is Event entity which is used to add event data")
public class Event {
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
	
	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	private Timestamp lastModificationDate;
	
	@JsonManagedReference
	@OneToOne(mappedBy = "event", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL) 
    private Template template;
	
//	@JsonManagedReference
//	@OneToOne(mappedBy = "event", fetch = FetchType.EAGER,
//            cascade = CascadeType.ALL) 
//    private EmailTemplate emailTemplate;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public Event(EventDto eventDto) 
	{
		this.eventName = eventDto.getEventName();
		this.eventType = eventDto.getEventType();
		this.description = eventDto.getDescription();
		this.status=eventDto.getStatus();
	}
}
