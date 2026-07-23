package com.savbill.inventorymanagement.security.spring;

import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.PropertyReaderUtil;
import com.savbill.inventorymanagement.core.constants.PGConstants;
import com.savbill.inventorymanagement.core.data.Auditable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.Properties;

public class AuditableListener {

    public static final String MODULE = "[AuditableListener]";

    @PrePersist
    public void setAuditParamsForSave(Object obj) {
        String SUBMODULE = MODULE + " [setAuditParamsForSave()] ";
        Auditable auditable = (Auditable) obj;
        LoggedInUser user;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if (securityContext.getAuthentication().getPrincipal().toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
                    Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
                    auditable.setLastModifiedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                    auditable.setLastModifiedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setCreatedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setCreatedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                }
                else {
                    user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
                    auditable.setCreatedById(user.getUserId());
                    auditable.setLastModifiedById(user.getUserId());
                    auditable.setCreatedByName(user.getFullName());
                    auditable.setLastModifiedByName(user.getFullName());
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
//            user = null;
        }
    }

    @PreUpdate
    @PreRemove
    public void setAuditParamsForUpdate(Object obj) {
        String SUBMODULE = MODULE + " [setAuditParamsForUpdate()] ";
        Auditable auditable = (Auditable) obj;
        LoggedInUser user;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if (securityContext.getAuthentication().getPrincipal().toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
                    Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
                    auditable.setLastModifiedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                    auditable.setLastModifiedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                }
                else {
                    user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
                    auditable.setLastModifiedById(user.getUserId());
                    auditable.setLastModifiedByName(user.getFullName());
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            user = null;
        }
    }
}
