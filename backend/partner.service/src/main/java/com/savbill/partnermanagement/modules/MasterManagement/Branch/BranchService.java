package com.savbill.partnermanagement.modules.MasterManagement.Branch;

import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.MasterManagement.BranchServiceAreaRel.BranchServiceAreaMapping;
import com.savbill.partnermanagement.modules.MasterManagement.BranchServiceAreaRel.BranchServiceAreaMappingRepository;
import com.savbill.partnermanagement.modules.MasterManagement.BranchServiceMapping.BranchServiceMappingEntity;
import com.savbill.partnermanagement.modules.MasterManagement.BranchServiceMapping.BranchServiceMappingRepository;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.rabbitmq.master.SaveBranchSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.master.UpdateBranchSharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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

    private static final Logger logger = LoggerFactory.getLogger(BranchService.class);

//    public void saveBranchEntity(SaveBranchSharedDataMessage message) throws Exception {
//        try {
//            Branch branch = new Branch();
//            branch.setId(message.getId());
//            branch.setName(message.getName());
//            branch.setStatus(message.getStatus());
//            branch.setBranch_code(message.getBranch_code());
//            if (message.getIsDeleted() == false) {
//     //           saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), false);
//                saveBranchServiceMapping(message.getBranchServiceMappingEntityList(), message.getId(), false);
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
//            logger.error("Unable to create branch details with name " + message.getName(), e.getMessage());
//        }
//    }

    @Transactional
    public void saveBranch(SaveBranchSharedDataMessage message){
        try {
            Branch branch = new Branch();
            branch.setId(message.getId());
            branch.setName(message.getName());
            branch.setBranch_code(message.getBranch_code());
            branch.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
            branch.setStatus(message.getStatus());
            branch.setRevenue_sharing(message.getRevenue_sharing());
            branch.setSharing_percentage(message.getSharing_percentage());
            branch.setDunningDays(message.getDunningDays());
           // branch.setServiceAreaNameList(message.getServiceAreaNameList());
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
            ApplicationLogger.logger.error("Unable to create  Branch With name"+ message.getName()+""+e.getMessage());
        }


    }

//    public void updateBranchEntity(UpdateBranchSharedData message) throws Exception {
//        try {
//            Branch branch = branchRepository.findById(message.getId()).orElse(null);
//            if (branch != null) {
//                branch.setId(message.getId());
//                branch.setName(message.getName());
//                branch.setStatus(message.getStatus());
//                branch.setBranch_code(message.getBranch_code());
//                if (message.getIsDeleted() == false) {
////                    saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), false);
//                    saveBranchServiceMapping(message.getBranchServiceMappingEntityList(), message.getId(), false);
//                } else {
// //                   saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), true);
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
////                    saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), false);
//                    saveBranchServiceMapping(message.getBranchServiceMappingEntityList(), message.getId(), false);
//                } else {
////                    saveBranchServiceAreaMapping(message.getServiceAreaNameList(), Math.toIntExact(message.getId()), true);
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
//            logger.error("Unable to update branch details with name " + message.getName(), e.getMessage());
//        }
//    }




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
                    if (!message.getBranchServiceMappingEntityList().isEmpty())
                        branch.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
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
                    branch1.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
                    branch1.setStatus(message.getStatus());
                    branch1.setRevenue_sharing(message.getRevenue_sharing());
                    branch1.setSharing_percentage(message.getSharing_percentage());
                    branch1.setDunningDays(message.getDunningDays());
                    //branch1.setServiceAreaNameList(message.getServiceAreaNameList());
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
            ApplicationLogger.logger.error("Unable to create  Branch "+e.getMessage());
        }
    }

    @Transactional
    public void saveBranchServiceAreaMapping(List<ServiceArea> serviceAreaList, Integer branchId, boolean isDeleted) {
        try {
            logger.info("Starting saveBranchServiceAreaMapping for branchId: " + branchId + ", isDeleted: " + isDeleted);
            List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByBranchId(branchId);
            logger.info("Existing branchServiceAreaMappings fetched: " + branchServiceAreaMappings);
            if (branchServiceAreaMappings.size() != 0) {
                List<Long> ids = branchServiceAreaMappings.stream().map(BranchServiceAreaMapping::getId).collect(Collectors.toList());
                logger.info("Mapping IDs to be considered for deletion logic: " + ids);
                ArrayList<Long> deletedItems = new ArrayList<>(ids);
                deletedItems.removeAll(branchServiceAreaMappings);
                logger.info("Computed deletedItems (possible issue): " + deletedItems);
                deletedItems.forEach(r -> {
                    logger.info("Deleting mapping with ID: " + r);
                    branchServiceAreaMappingRepository.deleteById(r);
                });
            }else {
                logger.info("No existing mappings found. Performing deleteAll with empty list.");
                branchServiceAreaMappingRepository.deleteAll(branchServiceAreaMappings);
            }
            if (!isDeleted) {
                logger.info("Creating new mappings for serviceAreaList: " + serviceAreaList);
                for (ServiceArea item : serviceAreaList) {
                    BranchServiceAreaMapping branchServiceAreaMapping = new BranchServiceAreaMapping();
                    branchServiceAreaMapping.setBranchId(Math.toIntExact(branchId));
                    branchServiceAreaMapping.setServiceareaId(Math.toIntExact(item.getId()));
                    logger.info("Saving mapping: " + branchServiceAreaMapping);
                    branchServiceAreaMappingRepository.save(branchServiceAreaMapping);
                }
            }
            logger.info("Completed saveBranchServiceAreaMapping for branchId: " + branchId);
        } catch (CustomValidationException e) {
            logger.error("Error in saveBranchServiceAreaMapping: " + e.getMessage(), e);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    @Transactional
    public void saveBranchServiceMapping(List<BranchServiceMappingEntity> branchServiceMappingEntityList, Long branchId, boolean isDelete) {
        try {
            logger.info("Starting saveBranchServiceMapping for branchId: " + branchId + ", isDelete: " + isDelete);
            List<BranchServiceMappingEntity> branchServiceMappingEntities = branchServiceMappingRepository.findAllByBranchId(branchId);
            logger.info("Existing branchServiceMappingEntities fetched: " + branchServiceMappingEntities);
            if (branchServiceMappingEntities.size() != 0) {
                List<Integer> ids = branchServiceMappingEntities.stream().map(BranchServiceMappingEntity::getId).collect(Collectors.toList());
                logger.info("Mapping IDs to be considered for deletion logic: " + ids);
                ArrayList<Integer> deletedItems = new ArrayList<>(ids);
                deletedItems.removeAll(branchServiceMappingEntities);
                logger.info("Computed deletedItems (possible issue): " + deletedItems);
                deletedItems.forEach(r -> {
                    logger.info("Deleting mapping with ID: " + r);
                    branchServiceMappingRepository.deleteById(r);
                });
            }else {
                logger.info("No existing mappings found. Performing deleteAll with empty list.");
                branchServiceMappingRepository.deleteAll(branchServiceMappingEntities);
            }
            if (!isDelete) {
                logger.info("Creating new mappings for branchServiceMappingEntityList: " + branchServiceMappingEntityList);
                for (BranchServiceMappingEntity item : branchServiceMappingEntityList) {
                    BranchServiceMappingEntity branchServiceMappingEntity = new BranchServiceMappingEntity();
                    branchServiceMappingEntity.setBranchId(branchId);
                    branchServiceMappingEntity.setServiceId(Math.toIntExact(item.getId()));
                    branchServiceMappingEntity.setRevenueShareper(item.getRevenueShareper());
                    branchServiceMappingEntity.setIsDeleted(item.getIsDeleted());
                    logger.info("Saving mapping: " + branchServiceMappingEntity);
                    branchServiceMappingRepository.save(branchServiceMappingEntity);
                }
            }
            logger.info("Completed saveBranchServiceMapping for branchId: " + branchId);
        } catch (CustomValidationException e) {
            logger.error("Error in saveBranchServiceMapping: " + e.getMessage(), e);
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }
}
