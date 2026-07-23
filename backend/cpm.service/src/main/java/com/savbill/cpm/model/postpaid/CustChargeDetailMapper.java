package com.savbill.cpm.model.postpaid;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.pojo.api.CustChargeDetailsPojo;
import com.savbill.cpm.service.postpaid.ChargeService;
import com.savbill.cpm.service.postpaid.CustChargeService;

@Mapper(componentModel = "spring",uses = {ChargeMapper.class})
public abstract class CustChargeDetailMapper implements IBaseMapper<CustChargeDetailsPojo, CustChargeDetails> {
    
    @Override
    public abstract CustChargeDetails dtoToDomain(CustChargeDetailsPojo dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "charge_date", target = "chargeDateString", dateFormat = "dd-MM-yyyy hh:mm a")
    @Mapping(source = "startdate", target = "startdateString", dateFormat = "dd-MM-yyyy hh:mm a")
    @Mapping(source = "enddate", target = "enddateString", dateFormat = "dd-MM-yyyy hh:mm a")
    @Mapping(source = "rev_date", target = "revdateString", dateFormat = "dd-MM-yyyy hh:mm a")
    @Mapping(source = "createdate", target = "createDateString", dateFormat = "dd-MM-yyyy hh:mm a")
    @Mapping(source = "updatedate", target = "updateDateString", dateFormat = "dd-MM-yyyy hh:mm a")
    public abstract CustChargeDetailsPojo domainToDTO(CustChargeDetails domain, @Context CycleAvoidingMappingContext context);

    @Autowired
    private CustChargeService custChargeService;

    @Autowired
    private ChargeService chargeService;

    @AfterMapping
    public void commonAfterPostmapping(@MappingTarget CustChargeDetailsPojo dto, CustChargeDetails domain) {
        if(domain.getChargeid()!=null){
            Charge charge = chargeService.get(domain.getChargeid());
            if(charge!=null && charge.getName()!=null){
                dto.setChargeName(charge.getName());
            }
        }
    }
}
