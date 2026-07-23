package com.savbill.partnermanagement.modules.PriceGroup.service;

import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBookSlabDetails;
import com.savbill.partnermanagement.modules.PriceGroup.mapper.PriceBookSlabDetailsMapper;
import com.savbill.partnermanagement.modules.PriceGroup.model.PriceBookSlabDetailsDTO;
import com.savbill.partnermanagement.modules.PriceGroup.repository.PriceBookSlabDetailsRepository;
import com.savbill.partnermanagement.modules.partner.entity.PriceBookSlabDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceBookSlabDetailsService extends ExBaseAbstractService<PriceBookSlabDetailsDTO, PriceBookSlabDetails,Long> {

    @Autowired
    private PriceBookSlabDetailsRepository priceBookSlabDetailsRepository;

    public PriceBookSlabDetailsService(PriceBookSlabDetailsRepository repository, PriceBookSlabDetailsMapper mapper) {
        super(repository, mapper);
    }

    public void IsDelete(PriceBookSlabDetails priceBookSlabDetail) throws Exception
    {
        priceBookSlabDetail.setDeleteFlag(true);
        priceBookSlabDetail.setId(priceBookSlabDetail.getId());
        priceBookSlabDetailsRepository.save(priceBookSlabDetail);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PriceBookSlabDetailsService]]";
    }
}
