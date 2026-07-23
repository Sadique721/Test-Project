package com.savbill.revenuemanagement.productmanagement.ServiceParameters.service;


import com.savbill.revenuemanagement.productmanagement.ServiceParameters.domain.ServiceParameter;
import com.savbill.revenuemanagement.productmanagement.ServiceParameters.repository.ServcieParametersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceParametersService {

//    public ServiceParametersService(ServcieParametersRepository repository, ServiceParametersMapper mapper) {
//        super(repository, mapper);
//    }
    private static final Logger logger = LoggerFactory.getLogger(ServiceParametersService.class);
    @Autowired
    ServcieParametersRepository servcieParametersRepository;

    public List<ServiceParameter> findall() {
       // String SUBMODULE = getModuleNameForLog() + " [findall()] ";
        //logger.info(getModuleNameForLog() + "--" + "  findall .Data[" + SUBMODULE.toString() + "]");
        try {
            return servcieParametersRepository.findAll();
        }catch (Exception exception){
           // logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

//    @Override
//    public String getModuleNameForLog() {
//        return "[ServiceParametersService]";
//    }
}
