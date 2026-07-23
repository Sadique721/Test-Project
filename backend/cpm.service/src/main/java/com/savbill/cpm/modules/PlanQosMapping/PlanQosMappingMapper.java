package com.savbill.cpm.modules.PlanQosMapping;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.PostpaidPlan;
import com.savbill.cpm.modules.qosPolicy.domain.QOSPolicy;
import com.savbill.cpm.modules.qosPolicy.mapper.QOSPolicyMapper;
import com.savbill.cpm.modules.qosPolicy.model.QOSPolicyDTO;
import com.savbill.cpm.modules.qosPolicy.service.QOSPolicyService;
import com.savbill.cpm.service.postpaid.PostpaidPlanService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class PlanQosMappingMapper implements IBaseMapper<PlanQosMappingPojo , PlanQosMappingEntity> {

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private QOSPolicyService qosPolicyService;

    @Autowired
    private QOSPolicyMapper qosPolicyMapper;

    @Mapping(source = "planid" , target = "postpaidPlan")
    @Mapping(source = "qosid" , target = "qosPolicy")
    public abstract PlanQosMappingEntity  dtoToDomain(PlanQosMappingPojo dto, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "postpaidPlan.id" , target = "planid")
    @Mapping(source = "qosPolicy.id" , target = "qosid")
    @Mapping(source = "qosPolicy.name" , target = "qosPolicyName")
    public abstract PlanQosMappingPojo domainToDTO(PlanQosMappingEntity data, @Context CycleAvoidingMappingContext context);

    PostpaidPlan fromIdToPostpaidPlan(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        PostpaidPlan entity;
        try {
            entity =  postpaidPlanService.findById(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    QOSPolicy fromIdToQosPolicy(Integer entityId){
        if (entityId == null) {
            return null;
        }
        QOSPolicy entity;

        try {
            QOSPolicyDTO qosPolicyDTO =  qosPolicyService.getEntityById(entityId.longValue());
            entity =  qosPolicyMapper.dtoToDomain( qosPolicyDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }


}
