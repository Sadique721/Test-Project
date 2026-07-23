package com.savbill.cpm.modules.Region.service;

import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.SaveRegionSharedDataMessage;
import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.UpdateRegionSharedDataMessage;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.Region.Mapper.RegionMapper;
import com.savbill.cpm.modules.Region.domain.Region;
import com.savbill.cpm.modules.Region.model.RegionDTO;
import com.savbill.cpm.modules.Region.repository.RegionBranchRepository;
import com.savbill.cpm.modules.Region.repository.RegionRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RegionService  extends ExBaseAbstractService<RegionDTO, Region, Long> {
//
//    public RegionService(RegionRepository repository, RegionMapper mapper) {
//        super(repository, mapper);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[RegionServiceService]";
//    }
//
//    @Autowired
//    RegionRepository repository;
//
//    @Autowired
//    RegionBranchRepository regionBranchRepository;
//
//    @Autowired
//    RegionMapper regionMapper;
//
//    @Override
//    public boolean duplicateVerifyAtSave(String rname) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (rname != null) {
//            rname = rname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(rname);
//            else count = repository.duplicateVerifyAtSave(rname, mvnoIds);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public Region getById(Long id) {
//        return repository.findById(id).get();
//    }
//
//    public boolean duplicateVerifyAtEdit(String rname, Long id) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (rname != null) {
//            rname = rname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(rname);
//            else count = repository.duplicateVerifyAtSave(rname, mvnoIds);
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    countEdit = repository.duplicateVerifyAtEdit(rname, id);
//                else countEdit = repository.duplicateVerifyAtEdit(rname, id, mvnoIds);
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getRegionByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    public GenericDataDTO getRegionByName(String rname, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            QRegion qRegion = QRegion.region;
//            BooleanExpression exp = qRegion.isNotNull();
//            exp = exp.and(qRegion.rname.containsIgnoreCase(rname)).or(qRegion.status.containsIgnoreCase(rname)).and(qRegion.isDeleted.eq(false));
//            Page<Region> regionList = null;
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                regionList = repository.findAll(exp, pageRequest);
//            } else {
//                exp = exp.and(qRegion.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                regionList = repository.findAll(exp, pageRequest);
//            }
//            if (null != regionList && 0 < regionList.getSize()) {
//                makeGenericResponse(genericDataDTO, regionList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//
//    @Override
//
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<Region> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
//        if (getMvnoIdFromCurrentStaff() == 1)
//            paginationList = repository.findAll(pageRequest);
//        else
//            paginationList = repository.findAll(pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//
//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = repository.deleteVerifyForBusinessVertical(Long.valueOf(id));
//        if (count == 0) {
//            flag = true;
//        }
//        return flag;
//    }
//
//    //Get All Region List By Branch
//    public List<RegionDTO>getAllRegionByServiceArea(List<Long> branchId) {
//        QRegionBranchMapping qRegionBranchMapping = QRegionBranchMapping.regionBranchMapping;
//        BooleanExpression exp = qRegionBranchMapping.isNotNull().and(qRegionBranchMapping.branchid.id.in(branchId));
//      List<RegionBranchMapping> regionBranchMappings = (List<RegionBranchMapping>) regionBranchRepository.findAll(exp);
//        List<Long> result = new ArrayList<>();
//        for (int i = 0; i < regionBranchMappings.size(); i++) {
//            result.add(regionBranchMappings.get(i).getRegionid().getId());
//        }
//        List<Region> regionList = repository.findAllByIdIn(result);
//        return regionList.stream().map(region -> regionMapper.domainToDTO(region, new CycleAvoidingMappingContext())).collect(Collectors.toList())
//                .stream().filter(x -> x.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toList())
//                .stream().filter(x -> x.getIsDeleted().equals(false)).collect(Collectors.toList());
//    }

    /* Region data from common microservice */


    public RegionService(RegionRepository repository, RegionMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RegionServiceService]";
    }
    private static Log log = LogFactory.getLog(RegionService.class);

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



    public void saveRegion(SaveRegionSharedDataMessage message) {
        // Create a new Region object
        try {
            Region region = new Region();
            // Set values from the message
            region.setId(message.getId());
            region.setRname(message.getRname());
            region.setBranchidList(message.getBranchidList());
            region.setStatus(message.getStatus());
            region.setIsDeleted(message.getIsDeleted());
            region.setMvnoId(message.getMvnoId());
            region.setCreatedById(message.getCreatedById());
            region.setLastModifiedById(message.getLastModifiedById());
            region.setCreatedByName(message.getCreatedByName());
            region.setLastModifiedByName(message.getLastModifiedByName());
            // Save the region using the repository
            repository.save(region);
        }catch (Exception e){
            log.error("Unable to create Region "+e.getMessage());
            e.getMessage();
        }

    }

    public void updateRegion(UpdateRegionSharedDataMessage message) {
        try {
            // Create a new Region object
           // Region region = new Region();
            // Find the existing object by id using the repository and assign it to the created object
            Region existingRegion = repository.findById(message.getId()).orElse(null);
            if (existingRegion != null) {
                // Set other values except id
                existingRegion.setRname(message.getRname());
                existingRegion.setBranchidList(message.getBranchidList());
                existingRegion.setStatus(message.getStatus());
                existingRegion.setIsDeleted(message.getIsDeleted());
                existingRegion.setMvnoId(message.getMvnoId());
                // Save the region using the repository
                existingRegion.setCreatedById(message.getCreatedById());
                existingRegion.setLastModifiedById(message.getLastModifiedById());
                existingRegion.setCreatedByName(message.getCreatedByName());
                existingRegion.setLastModifiedByName(message.getLastModifiedByName());
                repository.save(existingRegion);
            } else {
                Region region1 = new Region();
                // Set values from the message
                region1.setId(message.getId());
                region1.setRname(message.getRname());
                region1.setBranchidList(message.getBranchidList());
                region1.setStatus(message.getStatus());
                region1.setIsDeleted(message.getIsDeleted());
                region1.setMvnoId(message.getMvnoId());
                region1.setCreatedById(message.getCreatedById());
                region1.setLastModifiedById(message.getLastModifiedById());
                region1.setCreatedByName(message.getCreatedByName());
                region1.setLastModifiedByName(message.getLastModifiedByName());
                // Save the region using the repository
                repository.save(region1);
            }
        }catch (Exception e){
            log.error("Unable to Update Region "+e.getMessage());
        }
    }


}

