package com.savbill.commonGateway.moules.auditLog.mapper;


import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.auditLog.domain.AuditLogEntry;
import com.savbill.commonGateway.moules.auditLog.model.AuditLogEntryDTO;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public abstract class AuditLogMapper implements IBaseMapper<AuditLogEntryDTO, AuditLogEntry> {

    @AfterMapping
    void afterMapping(@MappingTarget AuditLogEntryDTO dto, AuditLogEntry data) {
        try {
            if (null != data.getOperation()) {
//                CacheManager cacheManager = CacheManager.getInstance();
//                Cache opCache = cacheManager.getCache("operationsCache");
//                Element el = opCache.get(data.getOperation());
                if (null != data.getOperation())
                    dto.setOperation((data.getOperation()));
                else
                    dto.setOperation("-");
            } else
                dto.setOperation("-");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(" AuditLogMapper After Mapping " + ex.getMessage(), ex);
        }
    }
}
