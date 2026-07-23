package com.savbill.cpm.modules.PriceGroup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.savbill.cpm.modules.PriceGroup.mapper.PriceBookPlanDtlMapper;
import com.savbill.cpm.modules.PriceGroup.model.PriceBookPlanDetailDTO;
import com.savbill.cpm.modules.PriceGroup.repository.PriceBookPlanDtlRepository;

@Service
public class PriceBookPlanDetailService extends ExBaseAbstractService<PriceBookPlanDetailDTO, PriceBookPlanDetail,Long>
{
    @Autowired

    PriceBookPlanDtlRepository priceBookPlanDtlRepository;

    public PriceBookPlanDetailService(PriceBookPlanDtlRepository repository, PriceBookPlanDtlMapper mapper) {
        super(repository, mapper);
    }

    public void planIsDelete(PriceBookPlanDetail priceBookPlanDetail) throws Exception
    {
        priceBookPlanDetail.setDeleteFlag(true);
        priceBookPlanDetail.setId(priceBookPlanDetail.getId());
        priceBookPlanDtlRepository.save(priceBookPlanDetail);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PriceBookPlanDetailsService]]";
    }
}
