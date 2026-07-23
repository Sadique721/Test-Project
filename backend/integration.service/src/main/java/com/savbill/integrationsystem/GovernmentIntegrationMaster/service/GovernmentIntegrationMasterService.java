package com.savbill.integrationsystem.GovernmentIntegrationMaster.service;

import com.savbill.integrationsystem.GovernmentIntegrationMaster.entity.GovernmentIntegrationMaster;
import com.savbill.integrationsystem.GovernmentIntegrationMaster.mapper.GovernmentIntegrationMasterMapper;
import com.savbill.integrationsystem.GovernmentIntegrationMaster.model.GovernmentIntegrationMasterDto;
import com.savbill.integrationsystem.GovernmentIntegrationMaster.repository.GovernmentIntegrationMasterRepository;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GovernmentIntegrationMasterService extends ExBaseAbstractService<GovernmentIntegrationMasterDto, GovernmentIntegrationMaster,Long> {
    public GovernmentIntegrationMasterService(GovernmentIntegrationMasterRepository repository, GovernmentIntegrationMasterMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    GovernmentIntegrationMasterRepository governmentIntegrationMasterRepository;

//    @Override
//    public boolean duplicateVerifyAtSave(String icName) throws Exception {
//        boolean flag = false;
//        //List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (icName != null) {
//            icName = icName.trim();
//            Integer count;
//            //if (getMvnoIdFromCurrentStaff() == 1) count = governmentIntegrationMasterRepository.duplicateVerifyAtSave(icName);
//            count = governmentIntegrationMasterRepository.duplicateVerifyAtSave(icName);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }

    public GovernmentIntegrationMaster getById(Long id) {
        //return governmentIntegrationMasterRepository.findById(id).get();
        return governmentIntegrationMasterRepository.findById(id).get();
    }

    @Override
    public GovernmentIntegrationMasterDto getEntityById(Long id, Long mvnoId) {
        try {
            GovernmentIntegrationMaster governmentIntegrationMaster = governmentIntegrationMasterRepository.getGovernmentIntegrationMasterByIdAndMvnoIdAndIsdeleteFalse(id, mvnoId);
            return getMapper().domainToDTO(governmentIntegrationMaster, new CycleAvoidingMappingContext());
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

//    public boolean duplicateVerifyAtEdit(String icname, Long id) throws Exception {
//        boolean flag = false;
//        if (icname != null) {
//            icname = icname.trim();
//            Integer count;
//            count = governmentIntegrationMasterRepository.duplicateVerifyAtSave(icname);
//            if (count >= 1) {
//                flag = true;
//            }
//        }
//        return flag;
//    }

//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getInvestmentCodeByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = governmentIntegrationMasterRepository.deleteVerifyForGovIntegrateMaster(Long.valueOf(id));
        if (count > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

//    @Override
//    public GovernmentIntegrationMasterDto updateEntity(GovernmentIntegrationMasterDto entity) {
//        return getMapper().domainToDTO(getRepository().save(getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext())), new CycleAvoidingMappingContext());
//    }
}
