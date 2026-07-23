package com.savbill.cpm.modules.tickets.service;

import com.savbill.cpm.core.service.ExBaseAbstractService;
//import com.savbill.cpm.modules.tickets.domain.CaseReason;
import com.savbill.cpm.modules.tickets.domain.CaseReasonConfig;
import com.savbill.cpm.modules.tickets.mapper.CaseReasonConfigMapper;
import com.savbill.cpm.modules.tickets.model.CaseReasonConfigPojo;
import com.savbill.cpm.modules.tickets.repository.CaseReasonConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseReasonConfigService extends ExBaseAbstractService<CaseReasonConfigPojo,CaseReasonConfig, Long> {

	@Autowired
    private CaseReasonConfigRepository caseReasonConfigRepository;
	
//	@Autowired
//    private CaseReasonService caseReasonConfigService;

    @Autowired
    private CaseReasonConfigMapper caseReasonConfigMapper;

    public CaseReasonConfigService(CaseReasonConfigRepository repository, CaseReasonConfigMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id","config_id");
        sortColMap.put("serviceArea","serviceareaid");
        sortColMap.put("staffUser","staffid");
        sortColMap.put("caseReason","reasonid");
    }

  
    @Override
    public String getModuleNameForLog() {
        return "[CaseReasonConfigService]";
    }

   
//	public List<CaseReasonConfigPojo> getEntityByCaseReasonId(Long caseReasonId) throws Exception {
//		CaseReason caseReason = caseReasonConfigService.getById(caseReasonId);
//		 List<CaseReasonConfig> caseReasonConfigList = caseReasonConfigRepository.findAllByCaseReason_ReasonIdAndIsDeleted(caseReason.getReasonId(),false);
//	        return caseReasonConfigList.stream().map(data ->
//	        caseReasonConfigMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//	}


//	public List<CaseReasonConfig> findAllByServiceAreaIdAndCaseReasonId(Long serviceAreaId,Long caseReasonId) {
//		return caseReasonConfigRepository.findAllByServiceArea_IdAndCaseReason_ReasonId(serviceAreaId,caseReasonId);
//	}

}
