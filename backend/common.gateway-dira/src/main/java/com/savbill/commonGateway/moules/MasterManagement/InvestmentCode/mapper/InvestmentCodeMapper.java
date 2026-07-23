package com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.mapper;


import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.DTO.InvestmentCodeDto;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.Domain.InvestmentCode;
import org.mapstruct.Mapper;

@Mapper
public abstract class InvestmentCodeMapper implements IBaseMapper<InvestmentCodeDto, InvestmentCode> {

    String MODULE = " [InvestmentCodeMapper] ";
}
