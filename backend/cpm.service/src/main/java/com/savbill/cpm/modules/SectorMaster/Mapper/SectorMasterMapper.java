package com.savbill.cpm.modules.SectorMaster.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.SectorMaster.Domain.SectorMaster;
import com.savbill.cpm.modules.SectorMaster.Model.SectorMasterDTO;
import org.mapstruct.Mapper;

@Mapper
public interface SectorMasterMapper extends IBaseMapper<SectorMasterDTO, SectorMaster> {
}
