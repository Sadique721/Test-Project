package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.service;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain.IPPoolDtls;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.mapper.IPPoolDtlsMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.IPPoolDtlsDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.repository.IPPoolDtlsRepository;

import java.util.ArrayList;
import java.util.List;

public class IpPoolThread implements Runnable{

    private List<IPPoolDtlsDTO> ipPoolDtlsDTO;
    private IPPoolDtlsMapper ipPoolDtlsMapper;
    private IPPoolDtlsRepository ipPoolDtlsRepository;

    public IpPoolThread(List<IPPoolDtlsDTO> ipPoolDtlsDTOList,IPPoolDtlsMapper ipPoolDtlsMapper,IPPoolDtlsRepository ipPoolDtlsRepository)
    {
        this.ipPoolDtlsDTO=ipPoolDtlsDTOList;
        this.ipPoolDtlsMapper=ipPoolDtlsMapper;
        this.ipPoolDtlsRepository=ipPoolDtlsRepository;
    }

    @Override
    public void run() {
        List<IPPoolDtls> ipPoolDtlsList=new ArrayList<>();
        for(int i=0;i<ipPoolDtlsDTO.size();i++)
        {
          IPPoolDtls ipPoolDtls=  ipPoolDtlsMapper.dtoToDomain(ipPoolDtlsDTO.get(i),new CycleAvoidingMappingContext());
            ipPoolDtlsList.add(ipPoolDtls);
        }
      //  List<IPPoolDtls> ipPoolDtlsList=  ipPoolDtlsDTO.stream().map(data->ipPoolDtlsMapper.dtoToDomain(data,new CycleAvoidingMappingContext())).collect(Collectors.toList());
        ipPoolDtlsRepository.saveAll(ipPoolDtlsList);
    }
}
