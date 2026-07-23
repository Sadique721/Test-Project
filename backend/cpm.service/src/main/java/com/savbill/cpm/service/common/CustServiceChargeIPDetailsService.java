package com.savbill.cpm.service.common;

import com.savbill.cpm.model.postpaid.CustServiceChargeIPDetails;
import com.savbill.cpm.pojo.api.CustServiceChargeIPDetailsPojo;
import com.savbill.cpm.repository.common.CustServiceChargeIPDetailsRepo;
import com.savbill.cpm.service.radius.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CustServiceChargeIPDetailsService extends AbstractService<CustServiceChargeIPDetails, CustServiceChargeIPDetailsPojo, Integer> {
    @Autowired
    private CustServiceChargeIPDetailsRepo entityRepository;

    @Override
    protected JpaRepository<CustServiceChargeIPDetails, Integer> getRepository() {
        return entityRepository;
    }
}
