package com.savbill.cpm.modules.InvestmentCode.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.InvestmentCode.DTO.InvestmentCodeDto;
import com.savbill.cpm.modules.InvestmentCode.Domain.InvestmentCode;
import org.mapstruct.Mapper;

@Mapper
public abstract class InvestmentCodeMapper implements IBaseMapper<InvestmentCodeDto, InvestmentCode> {

    String MODULE = " [InvestmentCodeMapper] ";
}
