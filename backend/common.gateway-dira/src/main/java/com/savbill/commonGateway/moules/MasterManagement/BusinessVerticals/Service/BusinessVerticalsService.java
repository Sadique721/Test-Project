package com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.Service;

import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.DTO.BusinessVerticalsDTO;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.Mapper.BusinessVerticalsMpper;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.Respository.BusinessVerticalsMappingRepository;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.Respository.BusinessVerticalsRepository;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain.BusinessVerticalsMapping;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain.QBusinessVerticals;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain.QBusinessVerticalsMapping;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessVerticalsService extends ExBaseAbstractService<BusinessVerticalsDTO, BusinessVerticals, Long> {

    public BusinessVerticalsService(BusinessVerticalsRepository repository, BusinessVerticalsMpper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[BusinessVerticalsService]";
    }

    @Autowired
    BusinessVerticalsRepository repository;

    @Autowired
    BusinessVerticalsMappingRepository businessVerticalsMappingRepository;

    @Autowired
    BusinessVerticalsMpper mapper;
    @Override
    public boolean duplicateVerifyAtSave(String vname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (vname != null) {
            vname = vname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(vname);
            else count = repository.duplicateVerifyAtSave(vname, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public BusinessVerticals getById(Long id) {
        return repository.findById(id).get();
    }

    public boolean duplicateVerifyAtEdit(String vname, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (vname != null) {
            vname = vname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(vname);
            else count = repository.duplicateVerifyAtSave(vname, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = repository.duplicateVerifyAtEdit(vname, id);
                else countEdit = repository.duplicateVerifyAtEdit(vname, id, mvnoIds);
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
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getBusinessVerticalsByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getBusinessVerticalsByName(String vname, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QBusinessVerticals qBusinessVerticals = QBusinessVerticals.businessVerticals;
            Page<BusinessVerticals> businessVerticalsList = null;
            BooleanExpression booleanExpression = qBusinessVerticals.isNotNull()
                    .and(qBusinessVerticals.isDeleted.eq(false))
                    .and(qBusinessVerticals.vname.likeIgnoreCase("%" + vname + "%").or(qBusinessVerticals.status.containsIgnoreCase(vname)));
            if(getMvnoIdFromCurrentStaff() == 1) {
                businessVerticalsList = repository.findAll(booleanExpression, pageRequest);
            }else {
                booleanExpression = booleanExpression.and(qBusinessVerticals.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                businessVerticalsList = repository.findAll(booleanExpression, pageRequest);
            }
            if (null != businessVerticalsList && 0 < businessVerticalsList.getSize()) {
                makeGenericResponse(genericDataDTO, businessVerticalsList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<BusinessVerticals> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = repository.findAll(pageRequest);
        else
            paginationList = repository.findAll(pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public void deleteBusinessVerticalMapping(Long id) {
        QBusinessVerticalsMapping qBusinessVerticalsMapping = QBusinessVerticalsMapping.businessVerticalsMapping;
        BooleanExpression booleanExpression = qBusinessVerticalsMapping.isDeleted.eq(false).and(qBusinessVerticalsMapping.businessVerticals.id.eq(id));
        List<BusinessVerticalsMapping> businessVerticalsMappings = IterableUtils.toList(businessVerticalsMappingRepository.findAll(booleanExpression));
        for (int i=0; i<businessVerticalsMappings.size(); i++) {
            businessVerticalsMappings.get(i).setIsDeleted(true);
            businessVerticalsMappingRepository.saveAll(businessVerticalsMappings);
        }
    }

    public List<BusinessVerticalsDTO> getAllVerticalsByRegion(List<Long> regionId) {
        QBusinessVerticalsMapping qBusinessVerticalsMapping = QBusinessVerticalsMapping.businessVerticalsMapping;
        BooleanExpression exp = qBusinessVerticalsMapping.isNotNull().and(qBusinessVerticalsMapping.isDeleted.eq(false));
        exp = exp.and(qBusinessVerticalsMapping.region.id.in(regionId));
       List<BusinessVerticalsMapping> businessVerticalsMappings = (List<BusinessVerticalsMapping>) businessVerticalsMappingRepository.findAll(exp);
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < businessVerticalsMappings.size(); i++) {
            result.add(businessVerticalsMappings.get(i).getBusinessVerticals().getId());
        }
        List<BusinessVerticals> businessVerticalsList = repository.findAllByIdIn(result);
        return businessVerticalsList.stream().map(x->mapper.domainToDTO(x,new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(y->y.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toList())
                .stream().filter(z->z.getIsDeleted().equals(false)).collect(Collectors.toList());
    }
}
