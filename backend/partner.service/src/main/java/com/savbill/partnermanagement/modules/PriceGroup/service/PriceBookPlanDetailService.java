package com.savbill.partnermanagement.modules.PriceGroup.service;

import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.PriceGroup.mapper.PriceBookPlanDtlMapper;
import com.savbill.partnermanagement.modules.PriceGroup.model.PriceBookPlanDetailDTO;
import com.savbill.partnermanagement.modules.PriceGroup.repository.PriceBookPlanDtlRepository;
import com.savbill.partnermanagement.modules.partner.entity.PriceBookPlanDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PriceBookPlanDetailService extends ExBaseAbstractService<PriceBookPlanDetailDTO, PriceBookPlanDetail,Long>
{
    @Autowired
    PriceBookPlanDtlRepository priceBookPlanDtlRepository;

    public PriceBookPlanDetailService(JpaRepository<PriceBookPlanDetail, Long> repository, PriceBookPlanDtlMapper mapper) {
        super(repository, mapper);
    }

    public void planIsDelete(PriceBookPlanDetail priceBookPlanDetail) throws Exception
    {
        ApplicationLogger.logger.info("planIsDelete Started : " + priceBookPlanDetail);
        priceBookPlanDetail.setDeleteFlag(true);
        priceBookPlanDetail.setId(priceBookPlanDetail.getId());
        priceBookPlanDtlRepository.save(priceBookPlanDetail);
        ApplicationLogger.logger.info("planIsDelete Completed : " + priceBookPlanDetail);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PriceBookPlanDetailsService]]";
    }
}
