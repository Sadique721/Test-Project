package com.savbill.ticketmanagement.core.modules.NetworkDevices.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.NetworkDevices.domain.NetworkDevices;
import com.savbill.ticketmanagement.core.modules.NetworkDevices.dto.NetworkDTO;
import org.mapstruct.Mapper;

@Mapper
public interface NetworkMapper extends IBaseMapper<NetworkDTO, NetworkDevices> {
}
