package com.savbill.ticketmanagement.core.modules.NetworkDevices.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.NetworkDevices.domain.OLTPortDetails;
import com.savbill.ticketmanagement.core.modules.NetworkDevices.dto.OLTPortDTO;
import org.mapstruct.Mapper;

@Mapper
public interface OltPortMapper extends IBaseMapper<OLTPortDTO, OLTPortDetails> {
}
