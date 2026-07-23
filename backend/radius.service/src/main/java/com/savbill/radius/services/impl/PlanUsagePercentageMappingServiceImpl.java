package com.savbill.radius.services.impl;

import com.savbill.radius.entity.PlanUsagePercentageMapping;
import com.savbill.radius.repository.PlanUsagePercentageMappingRepository;
import com.savbill.radius.services.PlanUsagePercentageMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanUsagePercentageMappingServiceImpl implements PlanUsagePercentageMappingService {

    @Autowired
    private PlanUsagePercentageMappingRepository planUsagePercentageMappingRepository;

    @Override
    public List<PlanUsagePercentageMapping> findPlanUsageMappingByPlanId(Integer planid){
        List<PlanUsagePercentageMapping> planUsagePercentageMappingList =  new ArrayList<>();
        planUsagePercentageMappingList = planUsagePercentageMappingRepository.findAllByPlanId(planid);
        if(planUsagePercentageMappingList.isEmpty()){
            planUsagePercentageMappingList = planUsagePercentageMappingRepository.findAllByPlanId(null);
        }
        return  planUsagePercentageMappingList;
    }

    @Override
    public PlanUsagePercentageMapping getPlanUsageMappinglevelBylevel(Integer planid , Integer id){
        PlanUsagePercentageMapping planUsagePercentageMapping = planUsagePercentageMappingRepository.findByPlanIdAndLevel(planid,id);
        if(planUsagePercentageMapping == null){
            planUsagePercentageMapping = planUsagePercentageMappingRepository.findByPlanIdAndLevel(null,id);
        }
        return planUsagePercentageMapping;
    }
}
