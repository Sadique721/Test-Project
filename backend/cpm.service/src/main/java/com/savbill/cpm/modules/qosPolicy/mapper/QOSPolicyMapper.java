package com.savbill.cpm.modules.qosPolicy.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.qosPolicy.domain.QOSPolicy;
import com.savbill.cpm.modules.qosPolicy.model.QOSPolicyDTO;
import org.mapstruct.*;

@Mapper
public abstract class QOSPolicyMapper implements IBaseMapper<QOSPolicyDTO, QOSPolicy> {

    @Override
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract QOSPolicyDTO domainToDTO(QOSPolicy data, @Context CycleAvoidingMappingContext context);

}
