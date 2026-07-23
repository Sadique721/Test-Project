package com.savbill.revenuemanagement.productmanagement.Tax.service;


import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;
import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxTypeTierPojo;
import com.savbill.revenuemanagement.productmanagement.Tax.mapper.TaxTypeTierMapper;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxTypeTierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TaxTypeTierService extends AbstractService<TaxTypeTier, TaxTypeTierPojo, Integer> {

    @Autowired
    private TaxTypeTierRepository entityRepository;
    @Autowired
    private TaxTypeTierMapper taxTypeTierMapper;

    @Override
    protected JpaRepository<TaxTypeTier, Integer> getRepository() {
        return entityRepository;
    }


}
