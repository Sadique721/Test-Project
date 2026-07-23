package com.savbill.cpm.modules.ServiceArea.service;

import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import com.savbill.cpm.modules.ServiceArea.mapper.ServiceAreaPincodeRelMapper;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaPincodeRelDTO;
import com.savbill.cpm.modules.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceAreaPincodeRelService extends ExBaseAbstractService<ServiceAreaPincodeRelDTO, ServiceAreaPincodeRel, Long> {

    public ServiceAreaPincodeRelService(ServiceAreaPincodeRelRepository repository, ServiceAreaPincodeRelMapper mapper) {
        super(repository, mapper);
    }


    @Override
    public String getModuleNameForLog() { return "[ServiceAreaPincodeRelService]"; }
}
