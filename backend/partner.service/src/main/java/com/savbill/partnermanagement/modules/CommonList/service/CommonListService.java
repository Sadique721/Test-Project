package com.savbill.partnermanagement.modules.CommonList.service;

import com.savbill.partnermanagement.constants.AuditLogConstants;
import com.savbill.partnermanagement.constants.CommonConstants;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.GenericSearchModel;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.CommonList.domain.CommonList;
import com.savbill.partnermanagement.modules.CommonList.mapper.CommonListMapper;
import com.savbill.partnermanagement.modules.CommonList.model.CommonListDTO;
import com.savbill.partnermanagement.modules.CommonList.repository.CommonListRepository;
import com.savbill.partnermanagement.modules.CommonList.utils.TypeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommonListService extends ExBaseAbstractService<CommonListDTO, CommonList, Long> {

    @Autowired
    private CommonListRepository commonListRepository;

    @Autowired
    private CommonListMapper commonListMapper;

    public CommonListService(CommonListRepository repository, CommonListMapper mapper) {
        super(repository, mapper);
        this.commonListMapper = mapper;
        this.commonListRepository = repository;
    }

    @Override
    public String getModuleNameForLog() {
        return "[CommonListService]";
    }

    public List<CommonListDTO> getCommonListByTypeWithoutCaching(String type) {
        return commonListRepository.findAllByTypeAndStatusOrderByValueAsc(type, CommonConstants.ACTIVE_STATUS)
                .stream().map(domain -> commonListMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }

    @Cacheable(cacheNames = "commonTypes", key = "#type")
    public List<CommonListDTO> getCommonListByType(String type) {
        return commonListRepository.findAllByTypeAndStatusOrderByValueAsc(type, CommonConstants.ACTIVE_STATUS)
                .stream().map(domain -> commonListMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "commonTypes", key = "#type")
    public List<CommonListDTO> getCommonListForAudit(String type) {
        String SUBMODULE = getModuleNameForLog() + " [getCommonListForAudit()] ";
        try {
            List<CommonListDTO> auditForList = getCommonListByType(type);
           ApplicationLogger.logger.info(SUBMODULE);
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                ApplicationLogger.logger.info(SUBMODULE + " Partner Id: " + getLoggedInUserPartnerId());
                return auditForList.stream().filter(dto -> !dto.getValue().equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_PARTNER))
                        .collect(Collectors.toList());
            }
            return auditForList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Cacheable(cacheNames = "allCommonTypes")
    public List<CommonListDTO> getAllEntities() throws Exception {
        return commonListRepository.findAllByStatus(CommonConstants.ACTIVE_STATUS)
                .stream().map(domain -> commonListMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
    public CommonListDTO saveEntity(CommonListDTO commonListDTO) throws Exception {
        return super.saveEntity(commonListDTO);
    }

    @CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
    public CommonListDTO updateEntity(CommonListDTO commonListDTO) throws Exception {
        return super.updateEntity(commonListDTO);
    }

    @CacheEvict(cacheNames = "commonTypes", key = "#commonListDTO.type")
    public void deleteEntity(CommonListDTO commonListDTO) throws Exception {
        super.deleteEntity(commonListDTO);
    }


    public String concatMethod(String mode) {
ApplicationLogger.logger.info("mode: "+mode);
        String newMode = mode.toLowerCase();
        if (newMode.equalsIgnoreCase("online")){
            ApplicationLogger.logger.info("online");
            String online = TypeConstants.CUSTDOCVERIFICATIONMODE_ONLINE;
            ApplicationLogger.logger.info("online: "+online);
            return online;
        }else {
            ApplicationLogger.logger.info("offline");
            String offline = TypeConstants.CUSTDOCVERIFICATIONMODE_OFFLINE;
            ApplicationLogger.logger.info("offline: "+offline);
            return offline;
        }
    }

    public String concatMethod(String mode,String custdocsubtype) {
        try {
            String newMode = mode.toLowerCase();
            String newcustdocsubtype = custdocsubtype.toLowerCase();
            ApplicationLogger.logger.info("mode: " + mode + ", subtype: " + custdocsubtype);
            if (newMode.equalsIgnoreCase("online") && newcustdocsubtype.equalsIgnoreCase("proofofaddress")) {
                return TypeConstants.CUSTDOCSUBTYPE_PROOFOFADDRESS_ONLINE;
            } else if (newMode.equalsIgnoreCase("offline") && newcustdocsubtype.equalsIgnoreCase("proofofaddress")) {
                return TypeConstants.CUSTDOCSUBTYPE_PROOFOFADDRESS_OFFLINE;
            } else if (newMode.equalsIgnoreCase("offline") && newcustdocsubtype.equalsIgnoreCase("proofofidentity")) {
                return TypeConstants.CUSTDOCSUBTYPE_PROOFOFIDENTITY_OFFLINE;
            } else if (newMode.equalsIgnoreCase("online") && newcustdocsubtype.equalsIgnoreCase("proofofidentity")) {
                return TypeConstants.CUSTDOCSUBTYPE_PROOFOFIDENTITY_ONLINE;
            } else if (newMode.equalsIgnoreCase("offline") && newcustdocsubtype.equalsIgnoreCase("contract")) {
                return TypeConstants.CUSTDOCSUBTYPE_CONTRACT_OFFLINE;
            } else if (newMode.equalsIgnoreCase("offline") && newcustdocsubtype.equalsIgnoreCase("migration")) {
                return TypeConstants.CUSTDOCSUBTYPE_MIGRATION_OFFLINE;
            }
            return null;
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error in concatMethod: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
