package com.savbill.ticketmanagement.core.modules.BusinessUnit.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.BusinessUnit.domain.BusinessUnit;
import com.savbill.ticketmanagement.core.modules.BusinessUnit.model.BusinessUnitDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class BusinessUnitMapper implements IBaseMapper<BusinessUnitDTO, BusinessUnit> {

//    @Autowired
//    private InvestmentCodeService investmentCodeService;
//
//    @Autowired
//    private InvestmentCodeMapper investmentCodeMapper;
//
//    @Override
////    @Mapping(source ="dtoData.investmentcode_id",target="investmentCodeList")
//    public  abstract BusinessUnit dtoToDomain(BusinessUnitDTO dtoData, CycleAvoidingMappingContext context);
//
//    @Override
//    @Mapping(target = "displayId", source = "data.id")
//    @Mapping(target = "displayName", source = "data.buname")
////    @Mapping(source = "data.investmentCodeList", target = "investmentcode_id")
//    public abstract BusinessUnitDTO domainToDTO(BusinessUnit data,CycleAvoidingMappingContext context);
//
//    Long fromIcNameToId(InvestmentCode entity){return entity == null ? null : entity.getId();}
//
//    InvestmentCode fromIdToIcName(Long entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        InvestmentCode entity;
//        try {
//            InvestmentCodeDto entityDTO = investmentCodeService.getEntityById(entityId);
//            entity = investmentCodeMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//            entity.setId(entityId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
}
