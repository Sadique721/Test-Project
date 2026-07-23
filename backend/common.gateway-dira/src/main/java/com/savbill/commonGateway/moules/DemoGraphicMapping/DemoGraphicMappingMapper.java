package com.savbill.commonGateway.moules.DemoGraphicMapping;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.DemoGraphicMapping.domain.DemoGraphicMappingTable;
import com.savbill.commonGateway.moules.DemoGraphicMapping.model.DemoGraphicMappingDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class DemoGraphicMappingMapper implements IBaseMapper<DemoGraphicMappingDTO, DemoGraphicMappingTable> {
}
