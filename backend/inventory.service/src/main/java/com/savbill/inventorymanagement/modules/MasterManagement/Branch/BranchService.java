package com.savbill.inventorymanagement.modules.MasterManagement.Branch;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.MasterManagement.BranchServiceAreaRel.BranchServiceAreaMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.BranchServiceAreaRel.BranchServiceAreaMappingRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.BranchServiceMapping.BranchServiceMappingRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveBranchSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateBranchSharedData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchService extends ExBaseAbstractService<BranchDTO, Branch, Long> {
    public BranchService(BranchRepository repository, BranchMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[BranchService]";
    }

    @Autowired
    BranchRepository branchRepository;
    @Autowired
    BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
    @Autowired
    BranchServiceMappingRepository branchServiceMappingRepository;
    @Autowired
    BranchMapper branchMapper;

    private static final Logger logger = Logger.getLogger(BranchService.class);

//    @Transactional
//    public void saveBranchEntity(SaveBranchSharedDataMessage message) throws Exception {
//        try {
//            Branch branch = new Branch();
//            branch.setId(message.getId());
//            branch.setName(message.getName());
//            branch.setStatus(message.getStatus());
//            branch.setBranch_code(message.getBranch_code());
//            if (message.getIsDeleted() == false) {
//                saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), false);
//                saveBranchServiceMapping(message.getBranchServiceMappingEntityList(), Long.valueOf(message.getId()), false);
//            }
//            branch.setIsDeleted(message.getIsDeleted());
//            branch.setMvnoId(message.getMvnoId());
//            branch.setCreatedById(message.getCreatedById());
//            branch.setLastModifiedById(message.getLastModifiedById());
//            branch.setRevenue_sharing(message.getRevenue_sharing());
//            branch.setSharing_percentage(message.getSharing_percentage());
//            branch.setDunningDays(message.getDunningDays());
//            branchRepository.save(branch);
//            logger.info("Branch details created successfully with name " + message.getName());
//        } catch (CustomValidationException e) {
//            logger.error("Unable to create branch details with name " + message.getName() + " , Error: " + e.getMessage());
//        }
//    }

//    @Transactional
//    public void updateBranchEntity(UpdateBranchSharedData message) throws Exception {
//        try {
//            Branch branch = branchRepository.findById(message.getId()).orElse(null);
//            if (branch != null) {
//                branch.setId(message.getId());
//                branch.setName(message.getName());
//                branch.setStatus(message.getStatus());
//                branch.setBranch_code(message.getBranch_code());
//                if (message.getIsDeleted() == false) {
//                    saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), false);
//                    saveBranchServiceMapping(message.getBranchServiceMappingEntityList(), message.getId(), false);
//                } else {
//                    saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), true);
//                    saveBranchServiceMapping(message.getBranchServiceMappingEntityList(), message.getId(), true);
//                }
//                branch.setIsDeleted(message.getIsDeleted());
//                branch.setMvnoId(message.getMvnoId());
//                branch.setCreatedById(message.getCreatedById());
//                branch.setLastModifiedById(message.getLastModifiedById());
//                branch.setRevenue_sharing(message.getRevenue_sharing());
//                branch.setSharing_percentage(message.getSharing_percentage());
//                branch.setDunningDays(message.getDunningDays());
//                branchRepository.save(branch);
//                logger.info("Branch details updated successfully with name " + message.getName());
//            } else {
//                Branch branch1 = new Branch();
//                branch1.setId(message.getId());
//                branch1.setName(message.getName());
//                branch1.setStatus(message.getStatus());
//                branch1.setBranch_code(message.getBranch_code());
//                if (message.getIsDeleted() == false) {
//                    saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), false);
//                    saveBranchServiceMapping(message.getBranchServiceMappingEntityList(), message.getId(), false);
//                } else {
//                    saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), true);
//                    saveBranchServiceMapping(message.getBranchServiceMappingEntityList(), message.getId(), true);
//                }
//                branch1.setIsDeleted(message.getIsDeleted());
//                branch1.setMvnoId(message.getMvnoId());
//                branch1.setCreatedById(message.getCreatedById());
//                branch1.setLastModifiedById(message.getLastModifiedById());
//                branch1.setRevenue_sharing(message.getRevenue_sharing());
//                branch1.setSharing_percentage(message.getSharing_percentage());
//                branch1.setDunningDays(message.getDunningDays());
//                branchRepository.save(branch1);
//                logger.info("Barnch details updated successfully with name " + message.getName());
//            }
//        } catch (CustomValidationException e) {
//            logger.error("Unable to update branch details with name " + message.getName() + " , Error: " + e.getMessage());
//        }
//    }

    @Transactional
    public void saveBranchServiceAreaMapping(List<ServiceArea> serviceAreaList, Integer branchId, boolean isDeleted) {
        try {
            List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByBranchId(branchId);
            if (branchServiceAreaMappings.size() != 0) {
                List<Long> ids = branchServiceAreaMappings.stream().map(BranchServiceAreaMapping::getId).collect(Collectors.toList());
                ArrayList<Long> deletedItems = new ArrayList<>(ids);
                deletedItems.removeAll(branchServiceAreaMappings);
                deletedItems.forEach(r -> {
                    branchServiceAreaMappingRepository.deleteById(r);
                });
            }else {
                branchServiceAreaMappingRepository.deleteAll(branchServiceAreaMappings);
            }
            if (!isDeleted) {
                for (ServiceArea item : serviceAreaList) {
                    BranchServiceAreaMapping branchServiceAreaMapping = new BranchServiceAreaMapping();
                    branchServiceAreaMapping.setBranchId(Math.toIntExact(branchId));
                    branchServiceAreaMapping.setServiceareaId(Math.toIntExact(item.getId()));
                    branchServiceAreaMappingRepository.save(branchServiceAreaMapping);
                }
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    @Transactional
    public void saveBranch(SaveBranchSharedDataMessage message){
        try {
            Branch branch = new Branch();
            branch.setId(message.getId());
            branch.setName(message.getName());
            branch.setBranch_code(message.getBranch_code());
          //  branch.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
            branch.setStatus(message.getStatus());
            branch.setRevenue_sharing(message.getRevenue_sharing());
            branch.setSharing_percentage(message.getSharing_percentage());
            branch.setDunningDays(message.getDunningDays());
          //  branch.setServiceAreaNameList(message.getServiceAreaNameList());
            branch.setIsDeleted(message.getIsDeleted());
            branch.setMvnoId(message.getMvnoId());
            branch.setStatus(message.getStatus());
            branch.setCreatedById(message.getCreatedById());
            branch.setLastModifiedById(message.getLastModifiedById());
            branch.setCreatedByName(message.getCreatedByName());
            branch.setLastModifiedByName(message.getLastModifiedByName());
            branchRepository.save(branch);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("Unable to create  Branch With name"+ message.getName()+""+e.getMessage());
        }


    }

    @Transactional
    public void updateBranch(UpdateBranchSharedData message){
        try {
            Branch branch = new Branch();
            if(message.getId()!=null) {
                branch = branchRepository.findById(message.getId()).orElse(null);
                if(branch!=null) {
                    branch.setCreatedByName(message.getCreatedByName());
                    branch.setLastModifiedByName(message.getLastModifiedByName());
                    branch.setName(message.getName());
                    branch.setBranch_code(message.getBranch_code());
//                    if (!message.getBranchServiceMappingEntityList().isEmpty())
//                        branch.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
                    branch.setStatus(message.getStatus());
                    branch.setRevenue_sharing(message.getRevenue_sharing());
                    branch.setSharing_percentage(message.getSharing_percentage());
                    branch.setDunningDays(message.getDunningDays());
//                    if (!message.getServiceAreaNameList().isEmpty()) {
//                        branch.setServiceAreaNameList(message.getServiceAreaNameList());
//                    }
                    branch.setIsDeleted(message.getIsDeleted());
                    branch.setMvnoId(message.getMvnoId());
                    branch.setStatus(message.getStatus());
                    branch.setCreatedById(message.getCreatedById());
                    branch.setLastModifiedById(message.getLastModifiedById());
                    branchRepository.save(branch);
                    if (branch.getIsDeleted()) {
                        List<BranchServiceAreaMapping> areaMappings=branchServiceAreaMappingRepository.findAllByBranchId(branch.getId().intValue());
                        areaMappings.stream().forEach(x-> {
                            branchServiceAreaMappingRepository.deleteById(x.getId());
                        });
                    }
                }else{
                    Branch branch1 = new Branch();
                    branch1.setId(message.getId());
                    branch1.setName(message.getName());
                    branch1.setBranch_code(message.getBranch_code());
                   // branch1.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
                    branch1.setStatus(message.getStatus());
                    branch1.setRevenue_sharing(message.getRevenue_sharing());
                    branch1.setSharing_percentage(message.getSharing_percentage());
                    branch1.setDunningDays(message.getDunningDays());
                   // branch1.setServiceAreaNameList(message.getServiceAreaNameList());
                    branch1.setIsDeleted(message.getIsDeleted());
                    branch1.setMvnoId(message.getMvnoId());
                    branch1.setStatus(message.getStatus());
                    branch1.setCreatedById(message.getCreatedById());
                    branch1.setLastModifiedById(message.getLastModifiedById());
                    branch.setCreatedByName(message.getCreatedByName());
                    branch.setLastModifiedByName(message.getLastModifiedByName());
                    branchRepository.save(branch1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Unable to create  Branch "+e.getMessage());
        }
    }
    public List<BranchDTO> getAllBranchesByServieAreaId(List<Integer> serviceAreaId) {
        try {
            List<Branch> branches = new ArrayList<>();
            List<BranchDTO> branchDTOList = new ArrayList<>();
            List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(serviceAreaId);
            List<Integer> branchIds = branchServiceAreaMappings.stream().map(BranchServiceAreaMapping::getBranchId).collect(Collectors.toList());
            List<Long> longBranchIds = branchIds.stream().map(Integer::longValue).collect(Collectors.toList());
            if (getLoggedInUserId() == 1) {
                branches = branchRepository.findAllByIsDeletedIsFalseAndStatusAndIdIn(CommonConstants.ACTIVE_STATUS, longBranchIds);
            } else {
                branches = branchRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), longBranchIds);
            }
            if (!branches.isEmpty()) {
                branchDTOList = branches.stream().map(branch -> branchMapper.domainToDTO(branch, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            return branchDTOList;
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
    }
}
