package com.savbill.cpm.modules.customerDocDetails.model;

import java.time.LocalDate;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.pojo.api.CustomersPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerDocDetailsDTO extends Auditable implements IBaseDto {
    private Long docId;
    private Integer custId;
    private String docType;
    private String docSubType;
    private String remark;
    private String mode;
    private String docStatus;
    private String filename;
    private String uniquename;
    private Boolean isDelete = false;
    private String documentNumber;

    @JsonBackReference
    @ApiModelProperty(hidden = true)
    private CustomersPojo customer;
    
 //   private Integer mvnoId;
    
//    @DateTimeFormat(pattern = "dd-MM-yyyy")
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startDate;

//    @DateTimeFormat(pattern = "dd-MM-yyyy")
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate;

    private Integer nextTeamHierarchyMappingId;
    private Integer nextStaff;
    private Integer mvnoId;


    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return docId;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }


    private Long leadId;
	
	private String startDateAsString;

    private String endDateAsString;
    private Integer staffId;
}
