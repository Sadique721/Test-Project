package com.savbill.ticketmanagement.core.modules.Notification.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.Notification.domain.Notification;
import com.savbill.ticketmanagement.core.modules.Notification.model.NotificationDTO;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper extends IBaseMapper<NotificationDTO, Notification> {
}
