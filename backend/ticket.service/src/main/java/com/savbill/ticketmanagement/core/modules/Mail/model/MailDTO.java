package com.savbill.ticketmanagement.core.modules.Mail.model;


import com.savbill.ticketmanagement.core.dto.IBaseDto;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
public class MailDTO implements IBaseDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String receiver;
    private String sender;
    private String cc;
    private String summary;
    private String desc;
    private Boolean isDelete;
    private Long issueid;
    private String messageId;
    private String folder;
    private Boolean isNew;
    private String mailType;
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    public Long getBuId() {
        return null;
    }
}
