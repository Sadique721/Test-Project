package com.savbill.taskmanagement.core.modules.TicketFollowupDetail.model;

import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class TicketFollowupDetailDTO implements IBaseDto {

    private Long id;
    
    private String remark;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime remarkDate;
    
    private Boolean isDelete = false;
    
    private Long caseId;
    
    private Integer staffId;
    
    private Integer custId;
  
    private String caseTitle;
    
    private String staffUserName;
    
    private String customersName;

    private Integer mvnoId;

    private String caseNumber ;

    private String remarkType ;

    private Boolean isFromCustomer;

	@Override
	public Long getIdentityKey() {
		return id;
	}

	@Override
	public Integer getMvnoId() {
		return null;
	}

    @Override
    public Long getBuId() {
        return null;
    }

}
