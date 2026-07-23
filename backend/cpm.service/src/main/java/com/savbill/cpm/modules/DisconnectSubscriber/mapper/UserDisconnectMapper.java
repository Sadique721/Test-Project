package com.savbill.cpm.modules.DisconnectSubscriber.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.DisconnectSubscriber.domain.UserDisconnect;
import com.savbill.cpm.modules.DisconnectSubscriber.model.UserDiscoonectDTO;

@Mapper
public interface UserDisconnectMapper extends IBaseMapper<UserDiscoonectDTO, UserDisconnect> {
}
