package com.savbill.cpm.modules.subscriber.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.subscriber.Domain.ServiceAudit;
import com.savbill.cpm.modules.subscriber.model.ServiceAuditDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceAuditMapper extends IBaseMapper<ServiceAuditDTO, ServiceAudit>{
}
