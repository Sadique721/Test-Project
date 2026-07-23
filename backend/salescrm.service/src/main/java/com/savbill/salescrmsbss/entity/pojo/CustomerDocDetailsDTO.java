package com.savbill.salescrmsbss.entity.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerDocDetailsDTO {

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
    
    private Integer mvnoId;
    
    private String startDateAsString;

    private String endDateAsString;

    private String createdByName;

    private Integer createdById;
    
    private Long leadId;
    private Integer staffId;
}
