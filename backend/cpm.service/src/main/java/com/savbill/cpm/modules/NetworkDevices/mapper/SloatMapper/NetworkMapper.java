package com.savbill.cpm.modules.NetworkDevices.mapper.SloatMapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.NetworkDevices.domain.NetworkDevices;
import com.savbill.cpm.modules.NetworkDevices.model.SloatModel.NetworkDTO;

@Mapper
public interface NetworkMapper extends IBaseMapper<NetworkDTO, NetworkDevices> {
}
