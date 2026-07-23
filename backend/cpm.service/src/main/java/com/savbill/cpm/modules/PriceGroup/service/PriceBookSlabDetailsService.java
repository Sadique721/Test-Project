package com.savbill.cpm.modules.PriceGroup.service;

import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.PriceGroup.domain.PriceBookSlabDetails;
import com.savbill.cpm.modules.PriceGroup.mapper.PriceBookSlabDetailsMapper;
import com.savbill.cpm.modules.PriceGroup.model.PriceBookSlabDetailsDTO;
import com.savbill.cpm.modules.PriceGroup.repository.PriceBookSlabDetailsRepository;
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
