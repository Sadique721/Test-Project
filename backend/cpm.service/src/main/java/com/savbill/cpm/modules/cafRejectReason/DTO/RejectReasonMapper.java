package com.savbill.cpm.modules.cafRejectReason.DTO;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.cafRejectReason.Entity.RejectReason;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class RejectReasonMapper implements IBaseMapper<RejectReasonDto, RejectReason>  {

    @Override
    @Mapping(source = "dtoData.rejectSubReasonDtoList", target = "rejectSubReasonList")
    public abstract RejectReason dtoToDomain(RejectReasonDto dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "data.rejectSubReasonList", target = "rejectSubReasonDtoList")
    public abstract RejectReasonDto domainToDTO(RejectReason data, CycleAvoidingMappingContext context);

    }


