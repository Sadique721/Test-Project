package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.billgen.entity.Branch;
import com.savbill.integrationsystem.billgen.entity.SaveBranchSharedDataMessage;
import com.savbill.integrationsystem.billgen.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchService {
    @Autowired
    BranchRepository branchRepository;
    public void save(SaveBranchSharedDataMessage message) {
        Branch branch=new Branch(message);
        branchRepository.save(branch);
    }
}
