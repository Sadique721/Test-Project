package com.savbill.integrationsystem.deviceveri.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.SerializedItemData;
import com.savbill.integrationsystem.deviceveri.mapper.SerializedItemMapper;
import com.savbill.integrationsystem.deviceveri.model.SerializedItemDTO;
import com.savbill.integrationsystem.deviceveri.repository.SerializedItemRepo;

@Service
public class SerializedItemService extends ExBaseAbstractService<SerializedItemDTO, SerializedItemData, Long> {


    @Autowired
    private SerializedItemRepo repo;

    @Autowired
    private SerializedItemMapper mapper;

    public SerializedItemService(SerializedItemRepo repo, SerializedItemMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "SerializedItemService[]";
    }
    
    public List<SerializedItemDTO> findBySerialNumberAndIsDeleted(String serialNum, Integer isDeleted) {
    	List<SerializedItemData> list = repo.findBySerialNumberAndIsDeleted(serialNum, isDeleted);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }
    
    public List<SerializedItemDTO> findByIdAndIsDeleted(Long id, Integer isDeleted){
    	Optional<SerializedItemData> optional = repo.findByIdAndIsDeleted(id, isDeleted);
    	List<SerializedItemDTO> list = new ArrayList<>();
    	if(optional.isPresent()) {
    		SerializedItemDTO dto = mapper.domainToDTO(optional.get(), new CycleAvoidingMappingContext());
    		list.add(dto);
    	}
    	return list;
    }
}
