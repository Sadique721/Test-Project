package com.savbill.taskmanagement.core.modules.ResolutionReasons.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.domain.ResolutionReasons;
import com.savbill.taskmanagement.core.modules.ResolutionReasons.model.ResolutionReasonsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ResolutionReasonsMapper extends IBaseMapper<ResolutionReasonsDTO, ResolutionReasons> {
}
