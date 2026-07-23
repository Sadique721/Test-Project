package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.pojo.CustomerDocDetailsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDocDetailsDTOMessage {

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
    
    private Integer mvnoId;
    
    private String startDateAsString;

    private String endDateAsString;

    
    public CustomerDocDetailsDTOMessage(CustomerDocDetailsDTO customerDocDetailsDTO) {
    	this.docId = customerDocDetailsDTO.getDocId();
    	this.custId = customerDocDetailsDTO.getCustId();
    	this.docType = customerDocDetailsDTO.getDocType();
    	this.docSubType = customerDocDetailsDTO.getDocSubType();
    	this.remark = customerDocDetailsDTO.getRemark();
    	this.mode = customerDocDetailsDTO.getMode();
    	this.docStatus = customerDocDetailsDTO.getDocStatus();
    	this.filename = customerDocDetailsDTO.getFilename();
    	this.uniquename = customerDocDetailsDTO.getUniquename();
    	this.documentNumber = customerDocDetailsDTO.getDocumentNumber();
    	this.mvnoId = customerDocDetailsDTO.getMvnoId();
    	this.startDateAsString = customerDocDetailsDTO.getStartDateAsString();
    	this.endDateAsString = customerDocDetailsDTO.getEndDateAsString();
    }
}
