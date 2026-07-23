package com.savbill.integrationsystem.deviceveri.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.PostpaidPlanData;
import com.savbill.integrationsystem.deviceveri.mapper.PostpaidPlanMapper;
import com.savbill.integrationsystem.deviceveri.model.PostpaidPlanDTO;
import com.savbill.integrationsystem.deviceveri.repository.PostpaidPlanRepo;

@Service
public class PostpaidPlanService extends ExBaseAbstractService<PostpaidPlanDTO, PostpaidPlanData, Long> {


    @Autowired
    private PostpaidPlanRepo repo;

    @Autowired
    private PostpaidPlanMapper mapper;

    public PostpaidPlanService(PostpaidPlanRepo repo, PostpaidPlanMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "PostpaidPlanService[]";
    }
    
    public List<PostpaidPlanDTO> findByPostpaidplanidAndIsDeleted(Long Postpaidplanid, Integer isDeleted){
    	List<PostpaidPlanData> list = repo.findByPostpaidplanidAndIsDeleted(Postpaidplanid, isDeleted);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
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
