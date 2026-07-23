package com.savbill.revenuemanagement.core.schedulers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SchedulerMapper {
    SchedulerManagement toEntity(SchedulerManagementDTO dto);

    SchedulerManagementDTO toDTO(SchedulerManagement entity);

    void updateEntityFromDto(SchedulerManagementDTO dto, @MappingTarget SchedulerManagement entity);

}
