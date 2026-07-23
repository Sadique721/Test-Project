package com.savbill.integrationsystem.NewNMSIntegration.service;

import com.savbill.integrationsystem.NewNMSIntegration.entity.IntegrationParameters;
import com.savbill.integrationsystem.NewNMSIntegration.repository.IntegrationParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IntegrationParametersService {

    @Autowired
    private IntegrationParametersRepository repository;

    public List<IntegrationParameters> getAllParameters() {
        return repository.findAll();
    }

    public Optional<IntegrationParameters> getParameterById(Long id) {
        return repository.findById(id);
    }

    public IntegrationParameters saveParameter(IntegrationParameters parameter) {
        return repository.save(parameter);
    }

    public void deleteParameter(Long id) {
        repository.deleteById(id);
    }
}


