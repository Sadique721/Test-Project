package com.savbill.cpm.modules.TechnicalDetails.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.TechnicalDetails.domain.TechnicalDetails;
import com.savbill.cpm.modules.TechnicalDetails.model.TechnicalDetailsDto;
import org.mapstruct.Mapper;

@Mapper
public interface TechnicalDetailsMapper extends IBaseMapper<TechnicalDetailsDto, TechnicalDetails> {
}
