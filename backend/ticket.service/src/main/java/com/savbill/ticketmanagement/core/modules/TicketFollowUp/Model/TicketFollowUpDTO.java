package com.savbill.ticketmanagement.core.modules.TicketFollowUp.Model;

import com.savbill.ticketmanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class TicketFollowUpDTO implements IBaseDto {

    private Long id;

    private String followUpName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime followUpDatetime;

    private String remarks;

    private String status;

    private Boolean isMissed = false;

    private Boolean isSend = false;

    private Integer caseId;

    private String caseNumber;

    private Integer createdBy;

    private Integer staffUserId;

    private String staffUserName;

    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public Long getBuId() {
        return null;
    }


}
