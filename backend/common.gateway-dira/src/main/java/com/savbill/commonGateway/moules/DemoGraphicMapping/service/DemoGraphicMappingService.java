package com.savbill.commonGateway.moules.DemoGraphicMapping.service;

import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.moules.DemoGraphicMapping.DemoGraphicMappingMapper;
import com.savbill.commonGateway.moules.DemoGraphicMapping.domain.DemoGraphicMappingTable;
import com.savbill.commonGateway.moules.DemoGraphicMapping.model.DemoGraphicMappingDTO;
import com.savbill.commonGateway.moules.DemoGraphicMapping.repository.DemoGraphicMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoGraphicMappingService extends ExBaseAbstractService<DemoGraphicMappingDTO, DemoGraphicMappingTable, Long> {

    @Autowired
    private DemoGraphicMappingRepository demoGraphicMappingRepository;

    public DemoGraphicMappingService(DemoGraphicMappingRepository repository, DemoGraphicMappingMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public JpaRepository<DemoGraphicMappingTable, Long> getRepository() {
        return demoGraphicMappingRepository;
    }

    @Override
    public String getModuleNameForLog() {
        return "[DemoGraphicMappingService]";
    }

    public List<DemoGraphicMappingTable> getAll(){
return demoGraphicMappingRepository.findAll();
    }

}
