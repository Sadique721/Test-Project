package com.savbill.taskmanagement.core.modules.Notification.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Notification.domain.Notification;
import com.savbill.taskmanagement.core.modules.Notification.model.NotificationDTO;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper extends IBaseMapper<NotificationDTO, Notification> {
}
