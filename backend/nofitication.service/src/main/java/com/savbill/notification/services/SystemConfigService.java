package com.savbill.notification.services;

import java.util.List;

import com.savbill.notification.entity.SystemConfig;
import com.savbill.notification.helper.SystemConfigDTO;

public interface SystemConfigService {
    List<SystemConfig> findAllSystemConfig(Long mvnoId, String key, String value, String serviceName);
    SystemConfig findStaffById(Long id,Long mvnoId);
    List<SystemConfig> searchSystemConfigByKey(String key,Long mvnoId);
    SystemConfig saveSystemConfig(SystemConfigDTO configDTO, Long mvnoId, boolean isFromCommon);
    SystemConfig updateSystemConfig(SystemConfigDTO configDTO, Long mvnoId, boolean isFromCommon);
    void saveAndUpdateSystemConfigFromCommon(SystemConfigDTO configDto, boolean isUpdate);
}
