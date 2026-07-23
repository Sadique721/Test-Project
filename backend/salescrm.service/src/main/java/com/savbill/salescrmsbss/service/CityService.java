package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.City;
import com.savbill.salescrmsbss.rabbitMq.message.SaveCitySharedDataMessage;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateCitySharedDataMessage;
import com.savbill.salescrmsbss.repository.CityRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CityService {


    @Autowired
    CityRepository cityRepository;
    private static Log log = LogFactory.getLog(CityService.class);


    @Transactional
    public void saveCityEntity(SaveCitySharedDataMessage message){
        try {
            City city = new City();
            city.setId(message.getId());
            city.setCountryId(message.getCountryId());
            city.setState(message.getState());
            city.setName(message.getName());
            city.setStatus(message.getStatus());
            city.setMvnoId(message.getMvnoId());
            city.setIsDelete(message.getIsDelete());

            cityRepository.save(city);
        }catch (Exception e){
            log.info("Unable to Create City with name "+message.getName()+" :"+e.getMessage());
        }

    }
    @Transactional
    public void updateCityEntity (UpdateCitySharedDataMessage message){
        try {
            if(message.getId()!=null) {
                City city = cityRepository.findById(message.getId()).orElse(null);
                if(city!=null){
                    city.setId(message.getId());
                    city.setCountryId(message.getCountryId());
                    city.setState(message.getState());
                    city.setName(message.getName());
                    city.setStatus(message.getStatus());
                    city.setMvnoId(message.getMvnoId());
                    city.setIsDelete(message.getIsDelete());

                    cityRepository.save(city);
                }else{
//                    log.info("No data Found");
                    City city1 = new City();
                    city1.setCountryId(message.getCountryId());
                    city1.setState(message.getState());
                    city1.setName(message.getName());
                    city1.setStatus(message.getStatus());
                    city1.setMvnoId(message.getMvnoId());
                    city1.setIsDelete(message.getIsDelete());

                    cityRepository.save(city1);
                }
            }
        }catch (Exception e){
            log.info("Unable to Update City ");
        }


    }
}
