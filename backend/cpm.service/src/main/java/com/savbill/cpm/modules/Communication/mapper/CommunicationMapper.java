package com.savbill.cpm.modules.Communication.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Communication.domain.Communication;
import com.savbill.cpm.modules.Communication.dto.CommunicationDTO;

@Mapper
public interface CommunicationMapper extends IBaseMapper<CommunicationDTO, Communication> {

}
