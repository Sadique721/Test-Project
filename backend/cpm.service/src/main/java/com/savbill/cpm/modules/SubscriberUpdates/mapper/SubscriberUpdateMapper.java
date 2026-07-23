package com.savbill.cpm.modules.SubscriberUpdates.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.SubscriberUpdates.domain.SubscriberUpdate;
import com.savbill.cpm.modules.SubscriberUpdates.model.SubscriberUpdateDTO;

@Mapper
public interface SubscriberUpdateMapper extends IBaseMapper<SubscriberUpdateDTO, SubscriberUpdate> {
}
