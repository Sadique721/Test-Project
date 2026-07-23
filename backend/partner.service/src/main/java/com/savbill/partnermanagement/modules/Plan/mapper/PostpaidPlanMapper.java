package com.savbill.partnermanagement.modules.Plan.mapper;

import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.modules.Charge.mapper.ChargeMapper;
import com.savbill.partnermanagement.modules.Plan.domain.PostpaidPlan;
import com.savbill.partnermanagement.modules.Plan.dto.PostpaidPlanPojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ChargeMapper.class})
public abstract class PostpaidPlanMapper {


//    @Mapping(source = "qospolicy", target = "qospolicyid")
//    @Mapping(source = "qospolicy.name", target = "qospolicyName")
//    @Mapping(source = "radiusprofile", target = "radiusprofileIds")
//    @Mapping(source = "createdate", target = "createDateString", dateFormat = "dd/MM/yyyy HH:mm a", defaultValue = "-")
//    @Mapping(source = "updatedate", target = "updateDateString", dateFormat = "dd/MM/yyyy HH:mm a", defaultValue = "-")
//    @Mapping(target = "displayId", source = "id")
//    @Mapping(target = "displayPostpaidName", source = "name")
//    //@Mapping(source = "planQosMappingEntities",target = "planQosMappingEntityList")
    public abstract PostpaidPlanPojo domainToDTO(PostpaidPlan data, @Context CycleAvoidingMappingContext context) throws NoSuchFieldException;

    public abstract List<PostpaidPlanPojo> domainToDTO(List<PostpaidPlan> data, @Context CycleAvoidingMappingContext context);

//    @Mapping(source = "qospolicyid", target = "qospolicy")
//    @Mapping(source = "radiusprofileIds", target = "radiusprofile")
    public abstract PostpaidPlan dtoToDomain(PostpaidPlanPojo dtoData, @Context CycleAvoidingMappingContext context) throws NoSuchFieldException;
//
//    @Autowired
//    private QOSPolicyService qosService;
//
//    @Autowired
//    private RadiusProfileService radiusProfileService;
//
//    //QOSPolicyService qosService; = SpringContext.getBean(QOSPolicyService.class);
//
//    private QOSPolicyMapper qosMapper = Mappers.getMapper(QOSPolicyMapper.class);
//
//    private static String MODULE = " [PostPaidPlanMapper] ";
//
//    //RadiusProfileIds(Integer) to RadiusProfile Mapping
//    public abstract List<RadiusProfile> mapRadiusProfileIdsToRadiusProfile(List<Integer> value);
//
//    public abstract List<Integer> mapRadiusProfileToRadiusProfileIds(List<RadiusProfile> value);
//
//    Integer fromRadiusprofile(RadiusProfile entity) {
//        return entity == null ? null : entity.getId();
//    }
//
//    RadiusProfile fromRadiusprofileIds(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        return radiusProfileService.get(entityId);
//    }
//
//
//    Long fromQospolicy(QOSPolicy entity) {
//        return entity == null ? null : entity.getId();
//    }
//
//    QOSPolicy fromQospolicyid(Long entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        QOSPolicy entity = null;
//        try {
//            QOSPolicyDTO entityDTO = qosService.getEntityById(entityId, false);
//            entity = qosMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//            entity.setId(entityId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }

}
