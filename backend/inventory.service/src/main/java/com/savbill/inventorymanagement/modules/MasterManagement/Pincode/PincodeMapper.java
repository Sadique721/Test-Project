package com.savbill.inventorymanagement.modules.MasterManagement.Pincode;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.MasterManagement.City.City;
import com.savbill.inventorymanagement.modules.MasterManagement.City.CityRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.Country;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.CountryRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.State.State;
import com.savbill.inventorymanagement.modules.MasterManagement.State.StateRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class PincodeMapper implements IBaseMapper<PincodeDTO, Pincode> {
    String MODULE = " [PincodeMapper] ";

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Override
    @Mapping(target = "id", source = "pincodeid")
    public abstract Pincode dtoToDomain(PincodeDTO pojo, @Context CycleAvoidingMappingContext context);

    @Mappings({
        @Mapping(target = "pincodeid", source = "id"),
        @Mapping(target = "displayId", source = "id"),
        @Mapping(target = "displayName", source = "pincode")
    })
    @Override
    public abstract PincodeDTO domainToDTO(Pincode domain, @Context CycleAvoidingMappingContext context);

    @AfterMapping
    void afterMapping(@MappingTarget PincodeDTO pincodeDTO, Pincode pincode) {
        try {
            if(pincode!=null){
                if(pincode.getCityId()!=null){
                    City city = cityRepository.findById(pincode.getCityId()).orElse(null);
                    if(city != null) {
                        pincodeDTO.setCityName(city.getName());
                    }
                }
                if(pincode.getStateId()!=null){
                    State state = stateRepository.findById(pincode.getStateId()).orElse(null);
                    if(state != null) {
                        pincodeDTO.setStateName(state.getName());
                    }
                }
                if(pincode.getCountryId()!=null){
                    Country country = countryRepository.findById(pincode.getCountryId()).orElse(null);
                    if(country != null) {
                        pincodeDTO.setCountryName(country.getName());
                    }
                }
//                if(!pincode.getAreaList().isEmpty()){
//                    StringBuilder stringBuilder = new StringBuilder("");
//                    pincode.getAreaList().forEach(data->{
//                        if(pincode.getAreaList().indexOf(data)==0){
//                            stringBuilder.append(data.getName());
//                        }
//                        else{
//                            stringBuilder.append(","+data.getName());
//                        }
//                    });
//                    pincodeDTO.setAreas(stringBuilder.toString());
//                }
            }
        }
        catch (Exception ex){
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
