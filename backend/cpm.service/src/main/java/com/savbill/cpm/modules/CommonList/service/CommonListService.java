package com.savbill.cpm.modules.CommonList.service;

import com.savbill.cpm.constants.AuditLogConstants;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.CommonList.domain.CommonList;
import com.savbill.cpm.modules.CommonList.mapper.CommonListMapper;
import com.savbill.cpm.modules.CommonList.model.CommonListDTO;
import com.savbill.cpm.modules.CommonList.repository.CommonListRepository;
import com.savbill.cpm.modules.CommonList.utils.TypeConstants;
import com.savbill.cpm.utils.CommonConstants;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
        CycleAvoidingMappingContext context = new CycleAvoidingMappingContext();
        return commonListRepository.findAllByTypeAndStatusOrderByValueAsc(type, CommonConstants.ACTIVE_STATUS)
                .stream()
                .map(domain -> commonListMapper.domainToDTO(domain, context))
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "commonTypes", key = "#type")
    public List<CommonListDTO> getCommonListForAudit(String type) {
        String SUBMODULE = getModuleNameForLog() + " [getCommonListForAudit()] ";
        try {
            List<CommonListDTO> auditForList = getCommonListByType(type);
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
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

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("CommonList");
        createExcel(workbook, sheet, CommonListDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, CommonListDTO.class, null);
    }

    public String concatMethod(String mode) {

        String newMode = mode.toLowerCase();
        if (newMode.equalsIgnoreCase("online")){
            String online = TypeConstants.CUSTDOCVERIFICATIONMODE_ONLINE;
            return online;
        }else {
            String offline = TypeConstants.CUSTDOCVERIFICATIONMODE_OFFLINE;
            return offline;
        }
    }

    public String concatMethod(String mode,String custdocsubtype) {
        try {
            String newMode = mode.toLowerCase();
            String newcustdocsubtype = custdocsubtype.toLowerCase();
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
            throw new RuntimeException(e);
        }
    }
}
