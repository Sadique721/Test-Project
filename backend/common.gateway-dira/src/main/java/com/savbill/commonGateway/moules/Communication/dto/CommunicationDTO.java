package com.savbill.commonGateway.moules.Communication.dto;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunicationDTO extends Auditable<Integer> implements IBaseDto {
    private Long id;
    private String email;
    private String subject;
    private String emailBody;
    private LocalDateTime scheduledTime;
    private String uuid;
    private String destination;
    private String source;
    private String smsMessage;
    private String templateId;
    private String channel;
    private Integer mvnoId;
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}
}
