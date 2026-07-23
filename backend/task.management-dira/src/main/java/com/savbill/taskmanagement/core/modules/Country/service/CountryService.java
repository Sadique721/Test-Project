package com.savbill.taskmanagement.core.modules.Country.service;

import com.savbill.taskmanagement.core.modules.Country.domain.Country;
import com.savbill.taskmanagement.core.modules.Country.dto.CountryPojo;
import com.savbill.taskmanagement.core.modules.Country.repository.CountryRepository;
import com.savbill.taskmanagement.core.service.AbstractService;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.SaveCountrySharedDataMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.UpdateCountrySharedDataMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class CountryService extends AbstractService<Country, CountryPojo, Integer> {

    @Autowired
    CountryRepository entityRepository;


    public static final String MODULE = "[CountryService]";


    @Override
    protected JpaRepository<Country, Integer> getRepository() {
        return entityRepository;
    }

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
                    log.info("No Data found");
                }
            }
        }
        catch (Exception e){
            log.info("Unable to Update Country with name "+message.getName()+" :"+e.getMessage());
        }
    }
    @Transactional
    public void saveCountrys(ConsumerRecord<String, Object> records) {
        try {
//            CountryRepository countryService = (CountryRepository) context.getBean(String.valueOf(CountryRepository.class));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = (String) records.value();
            Country country = new Country();
            SaveCountrySharedDataMessage countrySharedDataMessage = objectMapper.readValue(jsonString, SaveCountrySharedDataMessage.class);
                country.setName(countrySharedDataMessage.getName());
                country.setId(countrySharedDataMessage.getId());
                country.setMvnoId(countrySharedDataMessage.getMvnoId());
                country.setIsDelete(countrySharedDataMessage.getIsDelete());
                country.setStatus(countrySharedDataMessage.getStatus());
                entityRepository.save(country);
        } catch (Exception e) {
            log.info("Unable to Create Country with name "+" :"+e.getMessage());
        }
        System.out.println(records);
    }

    public void updateCountrys(ConsumerRecord<String, Object> records) {
        try {
//            CountryRepository countryService = (CountryRepository) context.getBean(String.valueOf(CountryRepository.class));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = (String) records.value();
            SaveCountrySharedDataMessage countrySharedDataMessage = objectMapper.readValue(jsonString, SaveCountrySharedDataMessage.class);
            if (countrySharedDataMessage.getId() != null) {
                Country country = entityRepository.findById(countrySharedDataMessage.getId()).orElse(null);
                if (country != null) {
                    country.setName(countrySharedDataMessage.getName());
                    country.setId(countrySharedDataMessage.getId());
                    country.setMvnoId(countrySharedDataMessage.getMvnoId());
                    country.setIsDelete(countrySharedDataMessage.getIsDelete());
                    country.setStatus(countrySharedDataMessage.getStatus());
                    entityRepository.save(country);
                } else {
                    log.info("No Data Found ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            log.info("Unable to Create Country with name "+message.getName()+" :"+e.getMessage());
        }
        System.out.println(records);
    }
}

