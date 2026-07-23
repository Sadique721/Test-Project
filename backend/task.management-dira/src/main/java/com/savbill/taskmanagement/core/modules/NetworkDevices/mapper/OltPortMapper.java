package com.savbill.taskmanagement.core.modules.NetworkDevices.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.NetworkDevices.domain.OLTPortDetails;
import com.savbill.taskmanagement.core.modules.NetworkDevices.dto.OLTPortDTO;
import org.mapstruct.Mapper;

@Mapper
public interface OltPortMapper extends IBaseMapper<OLTPortDTO, OLTPortDetails> {
}
