package com.savbill.cpm.modules.ResolutionReasons.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.ResolutionReasons.domain.ResolutionReasons;
import com.savbill.cpm.modules.ResolutionReasons.model.ResolutionReasonsDTO;

@Mapper
public interface ResolutionReasonsMapper extends IBaseMapper<ResolutionReasonsDTO, ResolutionReasons> {
}
