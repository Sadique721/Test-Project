package com.savbill.cpm.modules.Pincode.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.postpaid.City;
import com.savbill.cpm.model.postpaid.Country;
import com.savbill.cpm.model.postpaid.State;
import com.savbill.cpm.modules.Pincode.domain.Pincode;
import com.savbill.cpm.modules.Pincode.model.PincodeDTO;
import com.savbill.cpm.service.postpaid.CityService;
import com.savbill.cpm.service.postpaid.CountryService;
import com.savbill.cpm.service.postpaid.StateService;

@Mapper
public abstract class PincodeMapper implements IBaseMapper<PincodeDTO, Pincode> {
    String MODULE = " [PincodeMapper] ";
    @Autowired
    StateService stateService;
    @Autowired
    CountryService countryService;
    @Autowired
    CityService cityService;

    @Override
    @Mapping(target = "id", source = "pincodeid")
    public abstract Pincode dtoToDomain(PincodeDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    @Mapping(target = "pincodeid", source = "id")
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "pincode")
    public abstract PincodeDTO domainToDTO(Pincode domain, @Context CycleAvoidingMappingContext context);

    @AfterMapping
    void afterMapping(@MappingTarget PincodeDTO pincodeDTO, Pincode pincode) {
        try {
            if(pincode!=null){
                if(pincode.getCityId()!=null){
                    City city = cityService.get(pincode.getCityId());
                    if(city != null) {
                        pincodeDTO.setCityName(city.getName());
                    }
                }
                if(pincode.getStateId()!=null){

                    State state = stateService.get(pincode.getStateId());
                    if(state != null) {
                        pincodeDTO.setStateName(state.getName());
                    }
                }
                if(pincode.getCountryId()!=null){
                    Country country = countryService.get(pincode.getCountryId());
                    if(country != null) {
                        pincodeDTO.setCountryName(country.getName());
                    }
                }
                if(pincode.getAreaList().size()>0){
                    StringBuilder stringBuilder = new StringBuilder("");
                    pincode.getAreaList().forEach(data->{
                        if(pincode.getAreaList().indexOf(data)==0){
                            stringBuilder.append(data.getName());
                        }
                        else{
                            stringBuilder.append(","+data.getName());
                        }
                    });
                    pincodeDTO.setAreas(stringBuilder.toString());
                }
            }
        }
        catch (Exception ex){
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
