package com.savbill.cpm.modules.Broadcast.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Broadcast.domain.Broadcast;
import com.savbill.cpm.modules.Broadcast.model.BroadcastDTO;

@Mapper
public interface BroadcastMapper extends IBaseMapper<BroadcastDTO, Broadcast> {
}
