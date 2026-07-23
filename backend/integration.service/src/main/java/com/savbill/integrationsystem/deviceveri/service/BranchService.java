package com.savbill.integrationsystem.deviceveri.service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.BranchData;
import com.savbill.integrationsystem.deviceveri.mapper.BranchMapper;
import com.savbill.integrationsystem.deviceveri.model.BranchDTO;
import com.savbill.integrationsystem.deviceveri.repository.BranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("BranchServiceDeviceVeri")
public class BranchService extends ExBaseAbstractService<BranchDTO, BranchData, Long> {


    @Autowired
    private BranchRepo repo;

    @Autowired
    private BranchMapper mapper;

    public BranchService(BranchRepo repo, BranchMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "BranchService[]";
    }

    public Optional<BranchDTO> findById(Long id) {
        Optional<BranchData> optional = repo.findById(id);
        if(optional.isPresent()) {
            return Optional.of(mapper.domainToDTO(optional.get(), new CycleAvoidingMappingContext()));
        }
        return Optional.empty();
    }
}
