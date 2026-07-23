package com.savbill.inventorymanagement.modules.auditLog.mapper;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.auditLog.domain.AuditLogEntry;
import com.savbill.inventorymanagement.modules.auditLog.model.AuditLogEntryDTO;
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
