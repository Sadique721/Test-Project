package com.savbill.cpm.spring.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.savbill.cpm.constants.PGConstants;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.PropertyReaderUtil;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.Properties;

public class AuditableListener {

    public static final String MODULE = "[AuditableListener]";

    @PrePersist
    public void setAuditParamsForSave(Object obj) {
        String SUBMODULE = MODULE + " [setAuditParamsForSave()] ";
        LoggedInUser user;
        try {
            Auditable auditable = (Auditable) obj;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if (securityContext.getAuthentication().getPrincipal().toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
                    Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
                    auditable.setLastModifiedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                    auditable.setLastModifiedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setCreatedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setCreatedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                } else {
                    user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
                    auditable.setCreatedById(user.getUserId());
                    auditable.setLastModifiedById(user.getUserId());
                    auditable.setCreatedByName(user.getFullName());
                    auditable.setLastModifiedByName(user.getFullName());
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            user = null;
        }
    }

    @PreUpdate
    @PreRemove
    public void setAuditParamsForUpdate(Object obj) {
        String SUBMODULE = MODULE + " [setAuditParamsForUpdate()] ";
        LoggedInUser user;
        try {
            Auditable auditable = (Auditable) obj;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if (securityContext.getAuthentication().getPrincipal().toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
                    Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
                    auditable.setLastModifiedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                    auditable.setLastModifiedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                } else {
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
