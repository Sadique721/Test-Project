package com.savbill.cpm.modules.reports.recentrenewal.ReportProblem.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.reports.recentrenewal.ReportProblem.domain.ReportProblem;
import com.savbill.cpm.modules.reports.recentrenewal.ReportProblem.model.ReportProblemDTO;

@Mapper
public interface ReportProblemMapper extends IBaseMapper<ReportProblemDTO, ReportProblem> {
    public abstract ReportProblem dtoToDomain(ReportProblemDTO dtoData, @Context CycleAvoidingMappingContext context);



}




