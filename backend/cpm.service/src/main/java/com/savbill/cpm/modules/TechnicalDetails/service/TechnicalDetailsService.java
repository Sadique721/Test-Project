package com.savbill.cpm.modules.TechnicalDetails.service;

import com.savbill.cpm.core.service.ExBaseAbstractService2;
import com.savbill.cpm.modules.TechnicalDetails.domain.TechnicalDetails;
import com.savbill.cpm.modules.TechnicalDetails.mapper.TechnicalDetailsMapper;
import com.savbill.cpm.modules.TechnicalDetails.model.TechnicalDetailsDto;
import com.savbill.cpm.modules.TechnicalDetails.repository.TechnicalDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicalDetailsService extends ExBaseAbstractService2<TechnicalDetailsDto, TechnicalDetails, Long> {
    public TechnicalDetailsService(TechnicalDetailsRepository repository, TechnicalDetailsMapper mapper) {
        super(repository, mapper);
    }
    @Autowired
    private TechnicalDetailsRepository technicalDetailsRepository;

    @Autowired
    private TechnicalDetailsMapper technicalDetailsMapper;

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    public List<TechnicalDetails> getAll(){
        List<TechnicalDetails> technicalDetails = technicalDetailsRepository.findAll();
        return technicalDetails;
    }

}
