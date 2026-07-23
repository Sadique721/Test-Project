package com.savbill.commonGateway.moules.MasterManagement.Branch.service;


import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.*;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.Branch;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.BranchServiceAreaMapping;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.QBranch;
import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.QBranchServiceAreaMapping;
import com.savbill.commonGateway.moules.MasterManagement.Branch.mapper.BranchMapper;
import com.savbill.commonGateway.moules.MasterManagement.Branch.model.BranchDTO;
import com.savbill.commonGateway.moules.MasterManagement.Branch.model.BranchIdNameDTO;
import com.savbill.commonGateway.moules.MasterManagement.Branch.model.CustomBranchDTO;
import com.savbill.commonGateway.moules.MasterManagement.Branch.repository.BranchRepository;
import com.savbill.commonGateway.moules.MasterManagement.Branch.repository.BranchServiceAreaMappingRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.ServiceAreaService;
import com.savbill.commonGateway.spring.SpringContext;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BranchService extends ExBaseAbstractService<BranchDTO, Branch, Long> {


    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    BranchMapper branchMapper;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    public BranchService(@Lazy BranchRepository repository, @Lazy BranchMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "branchid");
        sortColMap.put("name", "name");
        sortColMap.put("status", "status");
    }

    @Override
    public String getModuleNameForLog() {
        return "[BranchService]";
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Branch> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1)
            paginationList = branchRepository.findAll(pageRequest);
        else
            paginationList = branchRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        paginationList.getContent().stream().map(data -> {
            List<Long> serviceAreaIds = branchServiceAreaMappingRepository.getAllServiceAreaIdsWithBranchId(data.getId().intValue()).stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            List<ServiceArea> serviceAreaList = serviceAreaRepository.getLightServiceAreaFromIds(serviceAreaIds);
            data.setServiceAreaNameList(new HashSet<>(serviceAreaList));
            return data;
        }).collect(Collectors.toList());
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = branchRepository.duplicateVerifyAtSave(name);
            else count = branchRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean verifyDuplicateCodeAtSave(String code) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (code != null) {
            code = code.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = branchRepository.findBranchCountFromBranchCode(code);
            else count = branchRepository.findBranchCountFromBranchCode(code, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    //Update Business Unit
    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = branchRepository.duplicateVerifyAtSave(name);
            else count = branchRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1) countEdit = branchRepository.duplicateVerifyAtEdit(name, id);
                else countEdit = branchRepository.duplicateVerifyAtEdit(name, id, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public boolean verifuDuplicateCodeAtEdit(String code, Long id) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (code != null) {
            code = code.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = branchRepository.findBranchCountFromBranchCode(code);
            else count = branchRepository.findBranchCountFromBranchCode(code, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1)
                    countEdit = branchRepository.findBranchCountFromBranchCodeAndId(code, id);
                else countEdit = branchRepository.findBranchCountFromBranchCodeAndIdAndMvnoId(code, id, mvnoIds);
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
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = branchRepository.deleteVerify(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    //   @Override
    public boolean deleteVerificationForRegion(Integer id) throws Exception {
        boolean flag = false;
        Integer count = branchRepository.deleteVerifyForRegion(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        return pageRequest;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "id", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getBranchByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        return null;
    }

    public GenericDataDTO getBranchByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getBranchByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<Branch> branchList = null;
//            QBranch qBranch = QBranch.branch;
//            BooleanExpression booleanExpression = qBranch.isNotNull()
//                    .and(qBranch.isDeleted.eq(false))
//                    .and(qBranch.name.likeIgnoreCase("%" + name + "%"))
//                    .or(qBranch.status.equalsIgnoreCase(name));
            if (getMvnoIdFromCurrentStaff() == 1) {
                branchList = branchRepository.findBranchByNameFiltered(name, pageRequest);
//                branchList = branchRepository.findAll(booleanExpression, pageRequest);
            } else {
                branchList = branchRepository.findBranchByNameFiltered(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                booleanExpression = booleanExpression.and(qBranch.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                branchList = branchRepository.findAll(booleanExpression, pageRequest);
            }
            if (null != branchList && 0 < branchList.getSize()) {
                makeGenericResponse(genericDataDTO, branchList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        return null;
    }

    public Branch getById(Long id) {
        return branchRepository.findById(id).get();
    }

    //Get All BranchId By ServiceAreas
    public List<Branch> getBranchByServiceArea() {
        try {
            QBranch qBranch = QBranch.branch;
            QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qBranch.isNotNull().and(qBranch.isDeleted.eq(false));
            if (getLoggedInUserId() != 1) {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                aBoolean = aBoolean
                        .and(qBranch.id.in(new JPAQuery[]{query.select(qBranchServiceAreaMapping.branchId)
                                        .from(qBranchServiceAreaMapping)
                                        .where(qBranchServiceAreaMapping.serviceareaId.in(serviceAreaIds))})
                                .and(qBranch.mvnoId.eq(getMvnoIdFromCurrentStaff())));
            }
            if (getMvnoIdFromCurrentStaff() != 1) {
                return IterableUtils.toList(branchRepository.findAll(aBoolean));
            } else {
                return branchRepository.findAll();
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    // Get All Service Area List By UserStaff
    public List<ServiceArea> getAllServiceAreaByBranchId(Integer branchId) {
        List<BranchServiceAreaMapping> branchServiceAreaMappingList = branchServiceAreaMappingRepository.findAllByBranchId(branchId);
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < branchServiceAreaMappingList.size(); i++) {
            result.add(Long.valueOf(branchServiceAreaMappingList.get(i).getServiceareaId()));
        }
        List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByIdIn(result);
        return serviceAreaList;
    }

    // Get All Branch List By ServiceArea
    public List<BranchDTO> getAllBranchesByServieAreaId(List<Integer> serviceAreaId) {
        //Find Branch List By Service Area Ids from BranchServiceAreaMapping
        List<Long> brnachIds = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(serviceAreaId,getMvnoIdFromCurrentStaff());
//        List<Long> result = new ArrayList<>();
//        for (int i = 0; i < branchServiceAreaMappingList.size(); i++) {
//            result.add(branchServiceAreaMappingList.get(i).getBranchId().longValue());
//        }
        List<Branch> branchList = branchRepository.findAllByIdIn(brnachIds);
        //Return active branch list
        return branchList.stream().map(branch -> branchMapper.domainToDTO(branch, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(branchDTO -> branchDTO.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toList())
                .stream().filter(branchDTO -> branchDTO.getIsDeleted().equals(false)).collect(Collectors.toList());
    }

    public List<BranchDTO> getAllBranchesByServiceAreaId(List<Integer> serviceAreaIds) {
        List<Long> branchIds = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(serviceAreaIds,getMvnoIdFromCurrentStaff()).stream().collect(Collectors.toList());
        Set<Long> filteredBrnachIds = new HashSet<>(branchIds);
        if (branchIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Branch> activeBranches = branchRepository.findAllActiveBranches(filteredBrnachIds, CommonConstants.ACTIVE_STATUS);
        return activeBranches.stream()
                .map(branch -> branchMapper.domainToDTO(branch, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    public List<BranchDTO> getAllBranchesByServieAreaIdWithSpecificParam(List<Integer> serviceAreaId) {
        //Find Branch List By Service Area Ids from BranchServiceAreaMapping
        List<Long> branchIds = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(serviceAreaId,getMvnoIdFromCurrentStaff());
        List<Branch> branchList = branchRepository.findAllByIdInWithSpecificParam(branchIds);
        //Return active branch list
        return branchList.stream()
                .map(branch -> branchMapper.domainToDTO(branch, new CycleAvoidingMappingContext()))
                .filter(branchDTO -> CommonConstants.ACTIVE_STATUS.equalsIgnoreCase(branchDTO.getStatus()))
                .filter(branchDTO -> !branchDTO.getIsDeleted())
                .collect(Collectors.toList());
    }


    public List<BranchIdNameDTO> getAllBranachesforPartnerByServiceAreaID(List<Integer> serviceAreaId) {

        try {
            //Find Branch List By Service Area Ids from BranchServiceAreaMapping
//            List<BranchServiceAreaMapping> branchServiceAreaMappingList = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(serviceAreaId);
            List<Integer>  branchServiceAreaMappingList = branchServiceAreaMappingRepository.findBranchIdsByServiceareaIdIn(serviceAreaId);
            List<Long> branchIds = new ArrayList<>();
            for (int i = 0; i < branchServiceAreaMappingList.size(); i++) {
                branchIds.add(branchServiceAreaMappingList.get(i).longValue());
            }
            boolean allEqual = true;
            if (serviceAreaId.size() > 1 && branchIds.size() > 1) {
                for (int i = 1; i < branchIds.size(); i++) {
                    if (!branchIds.get(i).equals(branchIds.get(0))) {
                        allEqual = false;
                        break;
                    }
                }
            }
            if (allEqual) {
                List<BranchIdNameDTO> branchList = branchRepository.findIdAndNameByStatusAndIsDeletedFalseAndIdIn("ACTIVE", branchIds);
                return branchList;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Page<Branch> getBranches(int page, int size) {
        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        return branchRepository.findAllProjectedBranch(pageable);
    }

//    @Override
//    @GetMapping(path = "/all")
    public List<BranchDTO> getAllBranchEntities() {
        List<Integer>mvnoIds=new ArrayList<>();
//        if(getMvnoIdFromCurrentStaff()!=1){
//            mvnoIds.add(getMvnoIdFromCurrentStaff());
//        }
        Integer currentMvnoId = getMvnoIdFromCurrentStaff();
        if (currentMvnoId != null && currentMvnoId != 1) {
            mvnoIds.add(currentMvnoId);
        }
        mvnoIds.add(1);
        QBranch qBranch =QBranch.branch;
        BooleanExpression booleanExpression=qBranch.isNotNull().and(qBranch.isDeleted.eq(false)).and(qBranch.mvnoId.in(mvnoIds));
//        if (Objects.nonNull(getBUIdsFromCurrentStaff()) && !getBUIdsFromCurrentStaff().isEmpty()) {
//            booleanExpression = booleanExpression.and(qBranch.bu.eq(getBUIdsFromCurrentStaff().get(0)));
//        }
//        List<Branch> branches= IterableUtils.toList(branchRepository.findAll(booleanExpression));


        JPQLQuery<BranchDTO> query = new JPAQuery<>(entityManager);
        return query.select(Projections.constructor(BranchDTO.class,
                        qBranch.id,
                        qBranch.name,
                        qBranch.status))
                .from(qBranch)
                .where(booleanExpression)
                .fetch();
    }

    public List<BranchDTO> getAllBranches() throws Exception {
        try {
            Integer currentStaffMvnoId = getMvnoIdFromCurrentStaff();

            // Call efficient repository method with filtering in DB
            List<Branch> filteredBranches = branchRepository.findByIsDeletedFalseAndMvnoIdIn(Arrays.asList(1, currentStaffMvnoId));

            // Map entities to DTOs
            return filteredBranches.stream()
                    .map(branch -> branchMapper.domainToDTO(branch, new CycleAvoidingMappingContext()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<CustomBranchDTO> getAllBranchesWithMVNOID() throws Exception{
        try {
            Integer currentStaffMvnoId = getMvnoIdFromCurrentStaff();
            List<Integer> mvnoIds = Arrays.asList(1, currentStaffMvnoId);
            List<CustomBranchDTO> branches = branchRepository.findBranchesBasic(mvnoIds);
            List<Long> branchIds = branches.stream().map(CustomBranchDTO::getId).collect(Collectors.toList());
            List<Object[]> saRows = branchRepository.findBranchServiceAreas(branchIds);
            Map<Long, CustomBranchDTO> branchMap = new HashMap<>();
            for (CustomBranchDTO b : branches) branchMap.put(b.getId(), b);
            for (Object[] row : saRows) {
                Long branchId = ((Number) row[0]).longValue();
                Long saId = ((Number) row[1]).longValue();
                String saName = row[2] != null ? (String) row[2] : "-";
                CustomBranchDTO dto = branchMap.get(branchId);
                if (dto != null) {
                    dto.getServiceAreaIdsList().add(saId);
                    dto.getServiceAreaNameList().add(saName);
                }
            }
            for (CustomBranchDTO b : branches) {
                b.setDisplayId(b.getId());
                b.setDisplayName(b.getName());
                if (b.getServiceAreaIdsList().isEmpty()) b.getServiceAreaNameList().add("-");
            }
            return branches;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }

    }
}

