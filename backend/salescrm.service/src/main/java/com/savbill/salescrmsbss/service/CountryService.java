package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.Country;
import com.savbill.salescrmsbss.rabbitMq.message.SaveCountrySharedDataMessage;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateCountrySharedDataMessage;
import com.savbill.salescrmsbss.repository.CountryRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CountryService {
    @Autowired
    private CountryRepository entityRepository;

    private static Log log = LogFactory.getLog(CountryService.class);


    @Transactional
    public void saveCountry (SaveCountrySharedDataMessage message){
        try {
            Country country = new Country();
            country.setId(message.getId());
            country.setName(message.getName());
            country.setStatus(message.getStatus());
            country.setMvnoId(message.getMvnoId());
            country.setIsDelete(message.getIsDelete());
            entityRepository.save(country);
        }catch (Exception e){
            log.info("Unable to Create Country with name "+message.getName()+" :"+e.getMessage());
        }


    }

    @Transactional
    public void updateCountry(UpdateCountrySharedDataMessage message) {
        try {
            if(message.getId()!=null) {
                Country country = entityRepository.findById(message.getId()).orElse(null);
                if(country!=null) {
                    country.setName(message.getName());
                    country.setStatus(message.getStatus());
                    country.setMvnoId(message.getMvnoId());
                    country.setIsDelete(message.getIsDelete());

                    entityRepository.save(country);
                }else{
//                    log.info("No Data found");
                    Country country1 = new Country();
                    country1.setId(message.getId());
                    country1.setName(message.getName());
                    country1.setStatus(message.getStatus());
                    country1.setMvnoId(message.getMvnoId());
                    country1.setIsDelete(message.getIsDelete());

                    entityRepository.save(country1);
                }
            }
        }
        catch (Exception e){
            log.info("Unable to Update Country with name "+message.getName()+" :"+e.getMessage());
        }
    }
}
