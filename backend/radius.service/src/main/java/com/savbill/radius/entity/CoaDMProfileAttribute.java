package com.savbill.radius.entity;

import com.savbill.radius.helper.CoaDMProfileAttributeDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "TBLTCOADMPROFILEATTRIBUTEMAPPING")
@ApiModel(value = "COA Profile Attribute Mapping Entity",description = "This is COA profile attribute entity which is used to update COA profile attribute data")
public class CoaDMProfileAttribute
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated COA Profile attribute Id")
    @Column (name="coadmprofileattributemappingid", nullable = false)
	private Long coaDMProfileAttributeMappingId;

	@ApiModelProperty(notes = "This is COA Profile id")
    @Column (name="coadmprofileid", nullable = false , length = 250)
    private Long coaDMProfileId;

	@ApiModelProperty(notes = "Radius Attribute of the COA Profile attribute")
    @Column (name="radiusatt", nullable = false , length = 15)
    private String radiusAtt;

	@ApiModelProperty(notes = "Profile Attribute of the COA Profile attribute")
    @Column (name="profileatt", nullable = false , length = 15)
    private String profileAtt;

	@ApiModelProperty(hidden = true)
	@Column (name="createdate")
	@JsonProperty("createDate")
	private Timestamp createdOn;

	@ApiModelProperty(hidden = true)
	@Column (name="lastmodificationdate")
	@JsonProperty("lastModificationDate")
	private Timestamp lastModifiedOn;

	@ApiModelProperty(hidden = true)
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	@ApiModelProperty(notes = "Check item for Profile Attribute")
	@Column (name="checkitem", nullable = false , length = 15)
	private String checkitem;

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	public Long getCoaDMProfileId() {
		return coaDMProfileId;
	}

	public void setCoaDMProfileId(Long coaDMProfileId) {
		this.coaDMProfileId = coaDMProfileId;
	}

	public Long getCoaDMProfileAttributeMappingId() {
		return coaDMProfileAttributeMappingId;
	}

	public void setCoaDMProfileAttributeMappingId(Long coaDMProfileAttributeMappingId) {
		this.coaDMProfileAttributeMappingId = coaDMProfileAttributeMappingId;
	}

	public String getRadiusAtt() {
		return radiusAtt;
	}

	public void setRadiusAtt(String radiusAtt) {
		this.radiusAtt = radiusAtt;
	}

	public String getProfileAtt() {
		return profileAtt;
	}

	public void setProfileAtt(String profileAtt) {
		this.profileAtt = profileAtt;
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

	public CoaDMProfileAttribute() {
		super();
	}

	public String getCheckitem() {
		return checkitem;
	}

	public void setCheckitem(String checkitem) {
		this.checkitem = checkitem;
	}

	public CoaDMProfileAttribute(CoaDMProfileAttributeDto coaDMProfileAttributeDto) {
		this.coaDMProfileId = coaDMProfileAttributeDto.getCoaDMProfileId();
		this.radiusAtt = coaDMProfileAttributeDto.getRadiusAtt();
		this.profileAtt = coaDMProfileAttributeDto.getProfileAtt();
		this.checkitem = coaDMProfileAttributeDto.getCheckitem();
	}
}
