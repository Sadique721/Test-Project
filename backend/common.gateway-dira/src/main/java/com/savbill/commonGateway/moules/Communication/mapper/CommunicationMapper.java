package com.savbill.commonGateway.moules.Communication.mapper;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.Communication.domain.Communication;
import com.savbill.commonGateway.moules.Communication.dto.CommunicationDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CommunicationMapper extends IBaseMapper<CommunicationDTO, Communication> {

}
