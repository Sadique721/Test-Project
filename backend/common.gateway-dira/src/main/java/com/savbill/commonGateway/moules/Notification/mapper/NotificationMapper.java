package com.savbill.commonGateway.moules.Notification.mapper;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.Notification.domain.Notification;
import com.savbill.commonGateway.moules.Notification.model.NotificationDTO;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper extends IBaseMapper<NotificationDTO, Notification> {
}
