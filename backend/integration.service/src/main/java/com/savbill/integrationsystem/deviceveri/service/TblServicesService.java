package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.ServicesData;
import com.savbill.integrationsystem.deviceveri.mapper.ServicesMapper;
import com.savbill.integrationsystem.deviceveri.model.ServicesDTO;
import com.savbill.integrationsystem.deviceveri.repository.ServicesRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TblServicesService extends ExBaseAbstractService<ServicesDTO, ServicesData, Long> {


    @Autowired
    private ServicesRepo repo;

    @Autowired
    private ServicesMapper mapper;

    public TblServicesService(ServicesRepo repo, ServicesMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "TblServicesService[]";
    }

    public List<ServicesDTO> findByServiceid(Long serviceId) {
        List<ServicesData> list = repo.findByServiceid(serviceId);
        return list.stream().map(servicesData -> mapper.domainToDTO(servicesData, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }
}
