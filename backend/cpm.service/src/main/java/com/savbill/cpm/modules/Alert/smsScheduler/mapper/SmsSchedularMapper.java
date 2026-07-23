package com.savbill.cpm.modules.Alert.smsScheduler.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Alert.smsScheduler.SchedularDTO.SmsSchedulerDTO;
import com.savbill.cpm.modules.Alert.smsScheduler.domain.SmsScheduler;

@Mapper
public interface SmsSchedularMapper extends IBaseMapper<SmsSchedulerDTO, SmsScheduler> {
}
