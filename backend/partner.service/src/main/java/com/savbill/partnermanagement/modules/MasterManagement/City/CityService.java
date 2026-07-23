package com.savbill.partnermanagement.modules.MasterManagement.City;

import com.savbill.partnermanagement.common.AbstractService;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.rabbitmq.master.SaveCitySharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.master.UpdateCitySharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService extends AbstractService<City, CityPojo, Integer> {

    @Autowired
    CityRepository cityRepository;


    private static final Logger logger = LoggerFactory.getLogger(CityService.class);

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
            logger.error("Unable to create city with name " + message.getName(), e.getMessage());
        }
    }

    public void updateCityEntity (UpdateCitySharedDataMessage message)throws Exception{
        try {
            logger.info("Updating City with name " + message.getName());
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
            logger.error("Unable to update city with name " + message.getName(), e.getMessage());
        }
    }

    @Override
    protected JpaRepository<City, Integer> getRepository() {
        return cityRepository;
    }
}
