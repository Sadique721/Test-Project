package com.savbill.ticketmanagement.core.modules.ResolutionReasons.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.ResolutionReasons.domain.ResolutionReasons;
import com.savbill.ticketmanagement.core.modules.ResolutionReasons.model.ResolutionReasonsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ResolutionReasonsMapper extends IBaseMapper<ResolutionReasonsDTO, ResolutionReasons> {
}
