package com.savbill.revenuemanagement.core.mapper.billrun;

import com.savbill.revenuemanagement.core.dto.billrun.BillRunPojo;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface BillRunMapper extends IBaseMapper<BillRunPojo, BillRun> {
    @Override
    public abstract BillRunPojo domainToDTO(BillRun data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract BillRun dtoToDomain(BillRunPojo dtoData, @Context CycleAvoidingMappingContext context);
}
