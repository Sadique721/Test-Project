package com.savbill.taskmanagement.core.modules.NetworkDevices.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.NetworkDevices.domain.NetworkDevices;
import com.savbill.taskmanagement.core.modules.NetworkDevices.dto.NetworkDTO;
import org.mapstruct.Mapper;

@Mapper
public interface NetworkMapper extends IBaseMapper<NetworkDTO, NetworkDevices> {
}
