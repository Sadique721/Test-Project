package com.savbill.cpm.modules.NetworkDevices.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.NetworkDevices.domain.NetworkDeviceBind;
import com.savbill.cpm.modules.NetworkDevices.model.NetworkDeviceBindDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class NetworkDeviceBindMapper implements IBaseMapper<NetworkDeviceBindDTO, NetworkDeviceBind> {
}
