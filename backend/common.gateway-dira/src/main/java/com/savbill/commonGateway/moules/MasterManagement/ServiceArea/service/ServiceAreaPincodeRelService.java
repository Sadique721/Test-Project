package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service;


import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceAreaPincodeRel;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.mapper.ServiceAreaPincodeRelMapper;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaPincodeRelDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceAreaPincodeRelService extends ExBaseAbstractService<ServiceAreaPincodeRelDTO, ServiceAreaPincodeRel, Long> {

    public ServiceAreaPincodeRelService(ServiceAreaPincodeRelRepository repository, ServiceAreaPincodeRelMapper mapper) {
        super(repository, mapper);
    }


    @Override
    public String getModuleNameForLog() { return "[ServiceAreaPincodeRelService]"; }
}
