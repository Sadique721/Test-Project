package com.savbill.cpm.modules.auditLog.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.auditLog.domain.AuditLogEntry;
import com.savbill.cpm.modules.auditLog.model.AuditLogEntryDTO;

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
