package com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.mapper;


import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.model.BusinessUnitDTO;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.DTO.InvestmentCodeDto;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.Domain.InvestmentCode;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.mapper.InvestmentCodeMapper;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.service.InvestmentCodeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class BusinessUnitMapper implements IBaseMapper<BusinessUnitDTO, BusinessUnit> {

    @Autowired
    private InvestmentCodeService investmentCodeService;

    @Autowired
    private InvestmentCodeMapper investmentCodeMapper;

    @Override
//    @Mapping(source ="dtoData.investmentcode_id",target="investmentCodeList")
    public  abstract BusinessUnit dtoToDomain(BusinessUnitDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mappings({@Mapping(target = "displayId", source = "data.id"),
            @Mapping(target = "displayName", source = "data.buname")})

//    @Mapping(source = "data.investmentCodeList", target = "investmentcode_id")
    public abstract BusinessUnitDTO domainToDTO(BusinessUnit data,CycleAvoidingMappingContext context);

    Long fromIcNameToId(InvestmentCode entity){return entity == null ? null : entity.getId();}

    InvestmentCode fromIdToIcName(Long entityId) {
        if (entityId == null) {
            return null;
        }
        InvestmentCode entity;
        try {
            InvestmentCodeDto entityDTO = investmentCodeService.getEntityById(entityId);
            entity = investmentCodeMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity.setId(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
