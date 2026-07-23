package com.savbill.taskmanagement.core.modules.tasks.mapper;


import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.ServiceArea.domain.ServiceArea;
import com.savbill.taskmanagement.core.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.savbill.taskmanagement.core.modules.ServiceArea.model.ServiceAreaDTO;
import com.savbill.taskmanagement.core.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseReasonConfig;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseReasonConfigPojo;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CaseReasonConfigMapper implements IBaseMapper<CaseReasonConfigPojo, CaseReasonConfig> {

    private String MODULE = " [CaseReasonConfigMapper] ";

//    @Mapping(source = "data.caseReason", target = "reasonid")
    @Mappings({
            @Mapping(source = "data.serviceArea", target = "serviceareaid"),
            @Mapping(source = "data.staffUser", target = "staffid")
    })
    public abstract CaseReasonConfigPojo domainToDTO(CaseReasonConfig data, @Context CycleAvoidingMappingContext context);

//    @Mapping(source = "dtoData.reasonid", target = "caseReason")
    @Mappings({
            @Mapping(source = "dtoData.serviceareaid", target = "serviceArea"),
            @Mapping(source = "dtoData.staffid", target = "staffUser")
    })

    public abstract CaseReasonConfig dtoToDomain(CaseReasonConfigPojo dtoData, @Context CycleAvoidingMappingContext context);


    @Autowired
    private StaffUserService staffUserService;

    Integer fromStaffUser(StaffUser entity) {
        return entity == null ? null : entity.getId();
    }

    StaffUser fromStaffUserId(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        StaffUser entity = null;
        try {
            entity = staffUserService.get(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    Long fromServiceArea(ServiceArea entity) {
        return entity == null ? null : entity.getId();
    }

    ServiceArea fromServiceAreaId(Long entityId) {
        if (entityId == null) {
            return null;
        }
        ServiceArea entity = null;
        try {
            ServiceAreaDTO dto = serviceAreaService.getEntityById(entityId, false);
            entity = serviceAreaMapper.dtoToDomain(dto, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    void afterMapping(@MappingTarget CaseReasonConfigPojo caseReasonConfigPojo, CaseReasonConfig caseReasonConfig) {
        try {
            if (null != caseReasonConfig.getServiceArea()) {
                ServiceArea serviceArea = caseReasonConfig.getServiceArea();
                caseReasonConfigPojo.setSericeAreaName(null != serviceArea && null != serviceArea.getName() ? serviceArea.getName() : "-");
            } else {
                caseReasonConfigPojo.setSericeAreaName("-");
            }
            if (null != caseReasonConfig.getStaffUser()) {
                StaffUser staffUser = caseReasonConfig.getStaffUser();
                caseReasonConfigPojo.setStaffUserName(null != staffUser && null != staffUser.getFullName() ? staffUser.getFullName() : "-");
            } else {
                caseReasonConfigPojo.setStaffUserName("-");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
