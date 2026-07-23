package com.savbill.commonGateway.moules.MasterManagement.Region.service;


import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Region.Mapper.RegionMapper;
import com.savbill.commonGateway.moules.MasterManagement.Region.domain.QRegion;
import com.savbill.commonGateway.moules.MasterManagement.Region.domain.QRegionBranchMapping;
import com.savbill.commonGateway.moules.MasterManagement.Region.domain.Region;
import com.savbill.commonGateway.moules.MasterManagement.Region.domain.RegionBranchMapping;
import com.savbill.commonGateway.moules.MasterManagement.Region.model.RegionDTO;
import com.savbill.commonGateway.moules.MasterManagement.Region.repository.RegionBranchRepository;
import com.savbill.commonGateway.moules.MasterManagement.Region.repository.RegionRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService  extends ExBaseAbstractService<RegionDTO, Region, Long> {

    public RegionService(RegionRepository repository, RegionMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RegionServiceService]";
    }

    @Autowired
    RegionRepository repository;

    @Autowired
    RegionBranchRepository regionBranchRepository;

    @Autowired
    RegionMapper regionMapper;

    @Override
    public boolean duplicateVerifyAtSave(String rname) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (rname != null) {
            rname = rname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(rname);
            else count = repository.duplicateVerifyAtSave(rname, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public Region getById(Long id) {
        return repository.findById(id).get();
    }

    public boolean duplicateVerifyAtEdit(String rname, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (rname != null) {
            rname = rname.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(rname);
            else count = repository.duplicateVerifyAtSave(rname, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = repository.duplicateVerifyAtEdit(rname, id);
                else countEdit = repository.duplicateVerifyAtEdit(rname, id, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }


    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getRegionByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getRegionByName(String rname, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QRegion qRegion = QRegion.region;
            BooleanExpression exp = qRegion.isNotNull();
            exp = exp.and(qRegion.rname.containsIgnoreCase(rname)).or(qRegion.status.containsIgnoreCase(rname)).and(qRegion.isDeleted.eq(false));
            Page<Region> regionList = null;
            if (getMvnoIdFromCurrentStaff() == 1) {
                regionList = repository.findAll(exp, pageRequest);
            } else {
                exp = exp.and(qRegion.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                regionList = repository.findAll(exp, pageRequest);
            }
            if (null != regionList && 0 < regionList.getSize()) {
                makeGenericResponse(genericDataDTO, regionList);
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
        Page<Region> paginationList = null;
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

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = repository.deleteVerifyForBusinessVertical(Long.valueOf(id));
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    //Get All Region List By Branch
    public List<RegionDTO>getAllRegionByServiceArea(List<Long> branchId) {
        QRegionBranchMapping qRegionBranchMapping = QRegionBranchMapping.regionBranchMapping;
        BooleanExpression exp = qRegionBranchMapping.isNotNull().and(qRegionBranchMapping.branchid.id.in(branchId));
      List<RegionBranchMapping> regionBranchMappings = (List<RegionBranchMapping>) regionBranchRepository.findAll(exp);
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < regionBranchMappings.size(); i++) {
            result.add(regionBranchMappings.get(i).getRegionid().getId());
        }
        List<Region> regionList = repository.findAllByIdIn(result);
        return regionList.stream().map(region -> regionMapper.domainToDTO(region, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(x -> x.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toList())
                .stream().filter(x -> x.getIsDeleted().equals(false)).collect(Collectors.toList());
    }
}

