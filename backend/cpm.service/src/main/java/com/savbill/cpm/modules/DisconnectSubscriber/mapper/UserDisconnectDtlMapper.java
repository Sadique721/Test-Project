package com.savbill.cpm.modules.DisconnectSubscriber.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.DisconnectSubscriber.domain.UserDisconnectDtl;
import com.savbill.cpm.modules.DisconnectSubscriber.model.UserDisconnectDtlDTO;

@Mapper
public interface UserDisconnectDtlMapper extends IBaseMapper<UserDisconnectDtlDTO, UserDisconnectDtl>
{}
