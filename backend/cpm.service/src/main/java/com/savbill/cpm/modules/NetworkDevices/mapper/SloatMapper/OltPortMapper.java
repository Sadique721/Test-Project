package com.savbill.cpm.modules.NetworkDevices.mapper.SloatMapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.NetworkDevices.domain.OLTPortDetails;
import com.savbill.cpm.modules.NetworkDevices.model.SloatModel.OLTPortDTO;

@Mapper
public interface OltPortMapper extends IBaseMapper<OLTPortDTO, OLTPortDetails> {
}
