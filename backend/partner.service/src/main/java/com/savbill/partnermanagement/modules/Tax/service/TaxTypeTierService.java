package com.savbill.partnermanagement.modules.Tax.service;

import com.savbill.partnermanagement.modules.Tax.mapper.TaxTypeTierMapper;
import com.savbill.partnermanagement.modules.Tax.repository.TaxTypeTierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxTypeTierService {

    @Autowired
    private TaxTypeTierRepository entityRepository;
    @Autowired
    private TaxTypeTierMapper taxTypeTierMapper;


}
