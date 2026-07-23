package com.savbill.ticketmanagement.core.auditLog.mapper;


import com.savbill.ticketmanagement.core.auditLog.domain.AuditLogEntry;
import com.savbill.ticketmanagement.core.auditLog.model.AuditLogEntryDTO;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
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
