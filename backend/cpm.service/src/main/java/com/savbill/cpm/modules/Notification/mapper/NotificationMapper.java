package com.savbill.cpm.modules.Notification.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Notification.domain.Notification;
import com.savbill.cpm.modules.Notification.model.NotificationDTO;

@Mapper
public interface NotificationMapper extends IBaseMapper<NotificationDTO, Notification> {
}
