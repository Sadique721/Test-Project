package com.savbill.cpm.modules.Cas.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Cas.Domain.CasMaster;
import com.savbill.cpm.modules.Cas.Model.CasMasterDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CasMapper extends IBaseMapper<CasMasterDTO, CasMaster> {
}