package com.savbill.commonGateway.moules.MasterManagement.Pincode.mapper;


import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.service.CityService;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeDTO;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Mappings({
            @Mapping(target = "pincodeid", source = "id"),
            @Mapping(target = "displayId", source = "id"),
            @Mapping(target = "displayName", source = "pincode")
    })

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
