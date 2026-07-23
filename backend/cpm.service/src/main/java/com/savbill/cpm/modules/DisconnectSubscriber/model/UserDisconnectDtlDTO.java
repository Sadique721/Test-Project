package com.savbill.cpm.modules.DisconnectSubscriber.model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.modules.DisconnectSubscriber.domain.UserDisconnect;

import lombok.Data;

@Data
public class UserDisconnectDtlDTO implements IBaseDto {
    private Long id;
    private String sessionid;
    private String NASIPAddress;
    private String FramedIPAddress;
    private UserDisconnect userDisconnect;
    private Boolean isDeleted = false;
    
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
