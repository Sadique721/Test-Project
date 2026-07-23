package com.savbill.salescrmsbss.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.ServiceParameter;
import com.savbill.salescrmsbss.repository.ServcieParametersRepository;

import java.util.List;

@Service
public class ServiceParametersService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceParametersService.class);
    @Autowired
    ServcieParametersRepository servcieParametersRepository;

    public List<ServiceParameter> findall() {
        try {
			return servcieParametersRepository.findAll();
		} catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
