package com.savbill.cpm.modules.ippool.service;

import com.savbill.cpm.constants.SearchConstants;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.repository.CustomRepository;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.ippool.domain.IPPool;
import com.savbill.cpm.modules.ippool.mapper.IPPoolMapper;
import com.savbill.cpm.modules.ippool.model.IPPoolDTO;
import com.savbill.cpm.modules.ippool.repository.IPPoolRepository;
import com.savbill.cpm.modules.subscriber.model.CustIPDetailsDTO;
import com.savbill.cpm.modules.subscriber.queryScript.IpExpiryScript;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPPoolService extends ExBaseAbstractService<IPPoolDTO, IPPool, Long> {

    @Autowired
    private IPPoolRepository ipPoolRepository;

    public IPPoolService(IPPoolRepository repository, IPPoolMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "pool_id");
        sortColMap.put("name", "pool_name");
        sortColMap.put("type", "pool_type");
        sortColMap.put("category", "pool_category");
    }

    @Autowired
    private CustomRepository<CustIPDetailsDTO> customRepository;

    @Override
    public String getModuleNameForLog() {
        return "[IPPoolService]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getPoolByNameOrTypeOrCategory(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("IPPool");
        createExcel(workbook, sheet, IPPoolDTO.class, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                IPPoolDTO.class.getDeclaredField("poolId"),
                IPPoolDTO.class.getDeclaredField("poolName"),
                IPPoolDTO.class.getDeclaredField("poolCategory"),
                IPPoolDTO.class.getDeclaredField("poolType"),
                IPPoolDTO.class.getDeclaredField("status"),
        };
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, IPPoolDTO.class, getFields());
    }

    public GenericDataDTO getPoolByNameOrTypeOrCategory(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getDeviceByNameOrTypeOrAreaName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<IPPool> ipPoolList;
            if(getMvnoIdFromCurrentStaff() == 1)
                ipPoolList = ipPoolRepository.findAllByPoolNameContainingIgnoreCaseOrPoolTypeContainingIgnoreCaseOrPoolCategoryContainingIgnoreCaseAndIsDeleteIsFalse(s1, s1, s1, pageRequest);
            else
                ipPoolList = ipPoolRepository.findAllByPoolNameContainingIgnoreCaseOrPoolTypeContainingIgnoreCaseOrPoolCategoryContainingIgnoreCaseAndIsDeleteIsFalse(s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != ipPoolList && 0 < ipPoolList.getSize()) {
                makeGenericResponse(genericDataDTO, ipPoolList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<CustIPDetailsDTO> getCustIpDetails(Long custId) {
        List<CustIPDetailsDTO> custIPList = customRepository.getResultOfQuery(IpExpiryScript.getIpDetails(custId), CustIPDetailsDTO.class);
        if (null != custIPList && 0 < custIPList.size()) {
            return custIPList;
        }
        return new ArrayList<>();
    }

    @Override
    public IPPoolDTO saveEntity(IPPoolDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        try {
            entity.setMvnoId(getMvnoIdFromCurrentStaff());
            if (entity.getDefaultPoolFlag()) {
                List<IPPoolDTO> defaultPoolList = getAllDefaultPool();
                if (null != defaultPoolList && 0 < defaultPoolList.size()) {
                    for (IPPoolDTO ipPoolDTO : defaultPoolList) {
                        ipPoolDTO.setDefaultPoolFlag(false);
                        updateEntity(ipPoolDTO);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(),ex);
            throw ex;
        }
        return super.saveEntity(entity);
    }

    @Override
    public boolean duplicateVerifyAtSave(String name)throws Exception
    {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = ipPoolRepository.duplicateVerifyAtSave(name);
            else count = ipPoolRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = ipPoolRepository.duplicateVerifyAtSave(name);
            else count = ipPoolRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = ipPoolRepository.duplicateVerifyAtEdit(name, id);
                else countEdit = ipPoolRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }


    @Override
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer count=ipPoolRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    public List<IPPoolDTO> getAllDefaultPool() {
        return ipPoolRepository.findAllByDefaultPoolFlagIsTrueAndIsDeleteIsFalse().stream().map(data -> getMapper()
                .domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<IPPool> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if(getMvnoIdFromCurrentStaff() == 1)
            paginationList = ipPoolRepository.findAll(pageRequest);
        else
            paginationList = ipPoolRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }
}
