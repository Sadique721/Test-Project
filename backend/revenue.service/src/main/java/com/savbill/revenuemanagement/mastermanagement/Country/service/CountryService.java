package com.savbill.revenuemanagement.mastermanagement.Country.service;


import com.savbill.revenuemanagement.mastermanagement.Country.domain.Country;
import com.savbill.revenuemanagement.mastermanagement.Country.repository.CountryRepository;

import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages.CountrySharedDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CountryService {

    @Autowired
    CountryRepository entityRepository;


    public static final String MODULE = "[CountryService]";



    private static Log log = LogFactory.getLog(CountryService.class);
    @Transactional
    public void saveCountry (CountrySharedDataMessage message){
        try {
            Country country = new Country();
            country.setId(message.getId());
            country.setName(message.getName());
            country.setStatus(message.getStatus());
            country.setMvnoId(message.getMvnoId());
            country.setIsDelete(message.getIsDelete());
            country.setCreatedById(message.getCreatedById());
            country.setLastModifiedById(message.getLastModifiedById());
            entityRepository.save(country);
        }catch (Exception e){
            log.info("Unable to Create Country with name "+message.getName()+" :"+e.getMessage());
        }


    }
@Transactional
    public void updateCountry(CountrySharedDataMessage message) {
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
                    log.info("No Data found");
                }
            }
        }
       catch (Exception e){
           log.info("Unable to Update Country with name "+message.getName()+" :"+e.getMessage());
       }
    }
}
