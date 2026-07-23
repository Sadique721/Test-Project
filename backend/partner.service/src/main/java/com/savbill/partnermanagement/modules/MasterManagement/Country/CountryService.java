package com.savbill.partnermanagement.modules.MasterManagement.Country;

import com.savbill.partnermanagement.common.AbstractService;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.rabbitmq.master.SaveCountrySharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.master.UpdateCountrySharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CountryService extends AbstractService<Country, Country, Integer> {

    @Autowired
    CountryRepository countryRepository;

    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);

    @Override
    protected JpaRepository<Country, Integer> getRepository() {
        return countryRepository;
    }
    public void saveCountry (SaveCountrySharedDataMessage message) throws Exception{
        logger.info("Saving country details with name " + message.getName());
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
            logger.error("Unable to create country details with name " + message.getName(), e.getMessage());
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
            logger.error("Unable to update country details with name " + message.getName(), e.getMessage());
        }
    }
}
