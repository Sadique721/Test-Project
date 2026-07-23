package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.billgen.entity.BillGenRawData;
import com.savbill.integrationsystem.billgen.mapper.BillGenMapper;
import com.savbill.integrationsystem.billgen.model.BillGenDTO;
import com.savbill.integrationsystem.billgen.repository.BillGenRepo;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillGenService extends ExBaseAbstractService<BillGenDTO, BillGenRawData, Integer> {


    @Autowired
    private BillGenRepo billGenRepo;

    @Autowired
    private BillGenMapper billGenMapper;

    public BillGenService(BillGenRepo billGenRepo, BillGenMapper mapper) {
        super(billGenRepo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "BillGenService[]";
    }

//    @Transactional
//    public void save(List<BillGenMessageData> message) {
//        for (BillGenMessageData billGenMessageData : message) {
//            BillGenRawData billGenRawData = new BillGenRawData(billGenMessageData);
//            billGenRepo.save(billGenRawData);
//        }
//
//    }


}
