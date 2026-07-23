package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tbltplanusagepercentagemapping")
@ApiModel(value = "PlanUsagePercentageMapping Entity",description = "This is Plan Usage entity mapping for fetch plan usage using entity")
@NoArgsConstructor
public class PlanUsagePercentageMapping
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated Entity id",required = true)
    @Column (name="id", nullable = false)
	private Long planUsageId;

	@ApiModelProperty(notes = "Plan Id")
	@Column (name="planid", nullable = false , length = 15)
	private Integer planId;

	@ApiModelProperty(notes = "Plan percentage")
	@Column (name="percentage", nullable = false , length = 15)
	private Double percentage;

	@ApiModelProperty(hidden = true)
	@Column (name="createdate")
	@JsonProperty("createDate")
	private Timestamp createdOn;

	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	@JsonProperty("lastModificationDate")
	private Timestamp lastModifiedOn;

	@Column(name = "level")
	private Integer level;


	public Long getPlanUsageId() {
		return planUsageId;
	}

	public void setPlanUsageId(Long planUsageId) {
		this.planUsageId = planUsageId;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}




	public Timestamp getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	public Timestamp getLastModifiedOn() {
		return lastModifiedOn;
	}
	public void setLastModifiedOn(Timestamp lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}


	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public PlanUsagePercentageMapping(Integer planId, Double percentage, Integer level) {
		this.planId = planId;
		this.percentage = percentage;
		this.level = level;
		this.createdOn = new Timestamp(System.currentTimeMillis());
		this.lastModifiedOn = new Timestamp(System.currentTimeMillis());
	}
}
