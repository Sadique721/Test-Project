package com.savbill.cpm.modules.planUpdate.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.PostpaidPlan;
import com.savbill.cpm.model.postpaid.PostpaidPlanMapper;
import com.savbill.cpm.modules.planUpdate.domain.QuotaDtls;
import com.savbill.cpm.modules.planUpdate.model.QuotaDtlsDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PostpaidPlanMapper.class})
public interface QuotaDtlsMapper extends IBaseMapper<QuotaDtlsDTO, QuotaDtls> {
    @Override
    @Mapping(source = "customers", target = "customersId")
    @Mapping(source = "postpaidPlan", target = "postpaidPlanId")
    QuotaDtlsDTO domainToDTO(QuotaDtls data, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "customers", source = "customersId")
    @Mapping(target = "postpaidPlan", source = "postpaidPlanId")
    QuotaDtls dtoToDomain(QuotaDtlsDTO dtoData, @Context CycleAvoidingMappingContext context);

    public abstract List<PostpaidPlan> mapPostpaidPlanIdToPostpaidPlan(List<Integer> value);

    public abstract List<Integer> mapPostpaidPlanToPostpaidPlanId(List<PostpaidPlan> value);

    default Long fromCustomers(Customers entity){
        return entity == null ? null : Long.valueOf(entity.getId());
    }

    default Customers fromCustomersIds(Integer entityId){
        final Customers customers = new Customers();
        customers.setId(entityId);
        return customers;
    }

    default Integer fromPostpaidPlan(PostpaidPlan entity){
        return entity == null ? null : entity.getId();
    }

    default PostpaidPlan fromPostpaidPlanId(Integer entityId){
        final PostpaidPlan plan = new PostpaidPlan();
        plan.setId(entityId);
        return plan;
    }
}
