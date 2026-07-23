package com.savbill.inventorymanagement.modules.MasterManagement.Country;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
//import com.savbill.inventorymanagement.rabbitmq.MessageReceiver;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveCountrySharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateCountrySharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryService extends ExBaseAbstractService<CountryPojo, Country, Integer> {

    @Autowired
    CountryRepository countryRepository;
    public CountryService(CountryRepository repository, CountryMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CountryService]";
    }
    private static final Logger logger = Logger.getLogger(CountryService.class);
    public void saveCountry (SaveCountrySharedDataMessage message) throws Exception{
        try {
            Country country = new Country();
            country.setId(message.getId());
            country.setName(message.getName());
            country.setStatus(message.getStatus());
            country.setMvnoId(message.getMvnoId());
            country.setIsDelete(message.getIsDelete());
            country.setCreatedById(message.getCreatedById());
            country.setLastModifiedById(message.getLastModifiedById());
            countryRepository.save(country);
            logger.info("Country details created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create country details with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public void updateCountry(UpdateCountrySharedDataMessage message) throws Exception {
        try {
            Country country = countryRepository.findById(message.getId()).orElse(null);
            if (country != null) {
                country.setName(message.getName());
                country.setStatus(message.getStatus());
                country.setMvnoId(message.getMvnoId());
                country.setIsDelete(message.getIsDelete());
                country.setCreatedById(message.getCreatedById());
                country.setLastModifiedById(message.getLastModifiedById());
                countryRepository.save(country);
                logger.info("Country details updated successfully with name " + message.getName());
            } else {
                Country country1 = new Country();
                country1.setId(message.getId());
                country1.setName(message.getName());
                country1.setStatus(message.getStatus());
                country1.setMvnoId(message.getMvnoId());
                country1.setIsDelete(message.getIsDelete());
                country1.setCreatedById(message.getCreatedById());
                country1.setLastModifiedById(message.getLastModifiedById());
                countryRepository.save(country1);
                logger.info("Country details updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update country details with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }
}
