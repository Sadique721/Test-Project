package com.savbill.inventorymanagement.modules.MasterManagement.City;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveCitySharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateCitySharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityService extends ExBaseAbstractService<CityPojo, City, Integer> {

    @Autowired
    CityRepository cityRepository;
    public CityService(CityRepository repository, CityMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CityService]";
    }
    private static final Logger logger = Logger.getLogger(CityService.class);

    public void saveCityEntity(SaveCitySharedDataMessage message) throws Exception{
        try {
            City city = new City();
            city.setId(message.getId());
            city.setCountryId(message.getCountryId());
            city.setStateId(message.getState().getId());
            city.setName(message.getName());
            city.setStatus(message.getStatus());
            city.setMvnoId(message.getMvnoId());
            city.setIsDelete(message.getIsDelete());
            city.setCreatedById(message.getCreatedById());
            city.setLastModifiedById(message.getLastModifiedById());
            cityRepository.save(city);
            logger.info("City created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create city with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public void updateCityEntity (UpdateCitySharedDataMessage message)throws Exception{
        try {
            City city = cityRepository.findById(message.getId()).orElse(null);
            if (city != null) {
                city.setId(message.getId());
                city.setCountryId(message.getCountryId());
                city.setStateId(message.getState().getId());
                city.setName(message.getName());
                city.setStatus(message.getStatus());
                city.setMvnoId(message.getMvnoId());
                city.setIsDelete(message.getIsDelete());
                city.setCreatedById(message.getCreatedById());
                city.setLastModifiedById(message.getLastModifiedById());
                cityRepository.save(city);
                logger.info("City updated successfully with name " + message.getName());
            } else {
                City city1 = new City();
                city1.setId(message.getId());
                city1.setCountryId(message.getCountryId());
                city1.setStateId(message.getState().getId());
                city1.setName(message.getName());
                city1.setStatus(message.getStatus());
                city1.setMvnoId(message.getMvnoId());
                city1.setIsDelete(message.getIsDelete());
                city1.setCreatedById(message.getCreatedById());
                city1.setLastModifiedById(message.getLastModifiedById());
                cityRepository.save(city1);
                logger.info("City updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update city with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }
}
