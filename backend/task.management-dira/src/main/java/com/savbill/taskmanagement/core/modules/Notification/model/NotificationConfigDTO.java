package com.savbill.taskmanagement.core.modules.Notification.model;


import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class NotificationConfigDTO implements IBaseDto {

    private Long id;
    private String config_entity;
    private String config_attribute;
    private String config_atrr_type;
    private String atrr_condi;
    private String attr_value;

    @JsonBackReference
    private NotificationDTO notification;
    private Integer mvnoId;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}

    @Override
    public Long getBuId() {
        return null;
    }
}
