package com.savbill.cpm.modules.Alert.emailSchedular.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Alert.emailSchedular.SchedularDTO.SchedulerDTO;
import com.savbill.cpm.modules.Alert.emailSchedular.domain.Scheduler;

@Mapper
public interface SchedularMapper extends IBaseMapper<SchedulerDTO, Scheduler> {
}
