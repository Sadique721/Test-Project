package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.model.postpaid.CustPlanMappping;
import com.savbill.cpm.repository.postpaid.CustPlanMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustPlanMappingReader {
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<CustPlanMappping> fetchUpdatedPlans(List<Integer> custServIds) {
        return custPlanMappingRepository.getAllByCustServiceMappingIds(custServIds);
    }
}
