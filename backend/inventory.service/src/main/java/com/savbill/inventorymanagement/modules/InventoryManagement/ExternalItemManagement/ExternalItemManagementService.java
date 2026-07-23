package com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.Customers.QCustomers;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.CommonResponceDto;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerRepository;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExternalItemManagementService extends ExBaseAbstractService<ExternalItemManagementDTO, ExternalItemManagement, Long> {

    public ExternalItemManagementService(ExternalItemManagementRepository repository, ExternalItemManagementMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ExternalItemManagementService]";
    }

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public ExternalItemManagementRepository externalItemManagementRepository;

//    @Autowired
//    StaffUserRepository staffUserRepository;

    @Autowired
    ExternalItemManagementMapper externalItemManagementMapper;

    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    ExternalItemMacSerialMappingRepo externalItemMacSerialMappingRepo;
    @Autowired
    ExternalItemMacSerialMappingService externalItemMacSerialMappingService;

//    @Autowired
//    CustomerMapper customerMapper;

    //Get External Item Group Details By Product and ServiceArea Id
    public List<ExternalItemManagement> getExtrenalItemDetailsByProductAndServiceAreaId(Long productId, Long serviceAreaId) {
        try {
            QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
            BooleanExpression booleanExpression = qExternalItemManagement.isNotNull().and(qExternalItemManagement.productId.id.eq(productId))
                    .and(qExternalItemManagement.status.eq(CommonConstants.ACTIVE_STATUS))
                    .and(qExternalItemManagement.serviceAreaId.id.eq(serviceAreaId))
                    .and(qExternalItemManagement.isDeleted.eq(false));
            return Lists.newArrayList(externalItemManagementRepository.findAll(booleanExpression))
                    .stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //Get List By Page And Size And SortBy And OrderBy
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        List<Long> resultPaginationList = new ArrayList<>();
        Page<ExternalItemManagement> finalPaginationList = null;
        String inwardNumber = null;
        QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
        BooleanExpression booleanExpression = qExternalItemManagement.isNotNull().and(qExternalItemManagement.isDeleted.eq(false));
        try {
            if (getMvnoIdFromCurrentStaff() != 1) {
                // Common method for find Service Area List Based on StaffId with Long
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Long> serviceAreaIds = serviceAreaService.getServiceAreaByStaffIdLong();
                if (!serviceAreaIds.isEmpty()) {
                    List<ExternalItemManagement> inwardServiceAreaStaffList = externalItemManagementRepository.findAllByServiceAreaIdIdInAndIsDeletedIsFalseAndMvnoIdIn(serviceAreaIds, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    if (inwardServiceAreaStaffList != null) {
                        if (inwardServiceAreaStaffList.size() > 0) {
                            for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                                resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                            }
                        }
                    }
                    finalPaginationList = externalItemManagementRepository.findAllByIdIn(resultPaginationList, pageRequest);
                } else {
                    List<Long> ids = externalItemManagementRepository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1))
                            .stream().map(ExternalItemManagement::getId)
                            .collect(Collectors.toList());
                    finalPaginationList = externalItemManagementRepository.findAllByIdIn(ids, pageRequest);
                }
            } else {
                finalPaginationList = externalItemManagementRepository.findAll(booleanExpression, pageRequest);
            }
            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    //Save
    @Transactional
    public ExternalItemManagementDTO saveEntity(ExternalItemManagementDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        ExternalItemManagementDTO externalItemManagementDTO = null;
        try {
            entity.setExternalItemGroupNumber(getRandomenumber("EX","-","",getMvnoIdFromCurrentStaff()));
            //entityDTO.unusedQty=entityDTO.getQty();
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            entity.setInTransitQty(entity.getInTransitQty());
            entity.setUsedQty(0L);
            entity.setRejectedQty(0L);
            entity.setApprovalStatus(CommonConstants.PENDING);
            entity.setTotalMacSerial(0L);
            externalItemManagementDTO = super.saveEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        }
        return externalItemManagementDTO;
    }

    //Get All External Item Group By Product And Staff
    public List<ExternalItemManagement> getAllExternalItemByProductAndStaff(Long productId, Long ownerId) {
        try {
            QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
            JPAQuery<ExternalItemManagement> query = new JPAQuery<>(entityManager);
            Long partnerId = Long.valueOf(getLoggedInUser().getPartnerId());
            List<ExternalItemManagement> externalItemManagementList = new ArrayList<>();
            BooleanExpression booleanExpression = qExternalItemManagement.isNotNull().
                    and(qExternalItemManagement.productId.id.eq(productId))
                    .and(qExternalItemManagement.isDeleted.eq(false))
                    .and(qExternalItemManagement.ownerId.in(ownerId, partnerId))
                    .and(qExternalItemManagement.approvalStatus.contains(CommonConstants.APPROVE));
            List<Tuple> result = query.select(qExternalItemManagement.id, qExternalItemManagement.externalItemGroupNumber, qExternalItemManagement.unusedQty, qExternalItemManagement.mvnoId).from(qExternalItemManagement).where(booleanExpression).fetch();
            if (!result.isEmpty()) {
                result.forEach(tuple -> {
                    ExternalItemManagement externalItemManagement = new ExternalItemManagement();
                    externalItemManagement.setId(tuple.get(qExternalItemManagement.id));
                    externalItemManagement.setExternalItemGroupNumber(tuple.get(qExternalItemManagement.externalItemGroupNumber));
                    externalItemManagement.setUnusedQty(tuple.get(qExternalItemManagement.unusedQty));
                    externalItemManagement.setMvnoId(tuple.get(qExternalItemManagement.mvnoId));
                    externalItemManagementList.add(externalItemManagement);
                });
            }
            if (getMvnoIdFromCurrentStaff() == 1)
                return externalItemManagementList;
            else
                return externalItemManagementList.stream().filter(externalItemManagement -> externalItemManagement.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || externalItemManagement.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //Update
    @Transactional
    public ExternalItemManagementDTO updateEntity(ExternalItemManagementDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [updateEntity()] ";
        ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(entity.getId()).get();
        ExternalItemManagementDTO externalItemManagementDTO = null;
        try {
            if (externalItemManagement.getApprovalStatus().equalsIgnoreCase(CommonConstants.PENDING)) {
                entity.setQty(0L);
                entity.setUnusedQty(0L);
                entity.setInTransitQty(entity.getInTransitQty());
                entity.setUsedQty(0L);
                entity.setRejectedQty(0L);
                entity.setApprovalStatus(CommonConstants.PENDING);
                if (entity.getTotalMacSerial() == externalItemManagement.getTotalMacSerial()) {
                    entity.setTotalMacSerial(externalItemManagement.getTotalMacSerial());
                }
                if (entity.getTotalMacSerial() != externalItemManagement.getTotalMacSerial()) {
                    entity.setTotalMacSerial(entity.getTotalMacSerial());
                }
            }
            externalItemManagementDTO = super.updateEntity(entity);
            //externalItemManagementDTO = super.saveEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return externalItemManagementDTO;
    }

    //Delete Verification
    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = externalItemManagementRepository.deleteVerify(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    public ExternalItemManagementDTO saveExternalItemGroupApproval(Long externalItemId, String externalItemGroupApprovalStatus, String approvalRemark) {
        ExternalItemManagementDTO externalItemManagementDTO = null;
        try {
            QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
            BooleanExpression booleanExpression = qExternalItemManagement.isNotNull()
                    .and(qExternalItemManagement.id.eq(externalItemId))
                    .and(qExternalItemManagement.unusedQty.eq(0L))
                    .and(qExternalItemManagement.qty.eq(0L))
                    .and(qExternalItemManagement.usedQty.eq(0L))
                    .and(qExternalItemManagement.rejectedQty.eq(0L))
                    .and(qExternalItemManagement.status.eq(CommonConstants.ACTIVE_STATUS))
                    .and(qExternalItemManagement.approvalStatus.contains(CommonConstants.PENDING))
                    .and(qExternalItemManagement.isDeleted.eq(false));

            List<ExternalItemManagement> externalItemManagementList = Lists.newArrayList(externalItemManagementRepository.findAll(booleanExpression))
                    .stream().filter(externalItemManagement -> externalItemManagement.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || externalItemManagement.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
            externalItemManagementDTO = updateExternalItemGroup(externalItemManagementList, externalItemGroupApprovalStatus, approvalRemark);
            if (externalItemGroupApprovalStatus.equalsIgnoreCase(CommonConstants.REJECTED)) {
                List<ExternalItemMacSerialMapping> externalItemMacSerialMappings = externalItemMacSerialMappingRepo.findAllByExternalItemId(externalItemId);
                for (ExternalItemMacSerialMapping item : externalItemMacSerialMappings) {
                    externalItemMacSerialMappingService.deleteExternalItemMac(item.getItemId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return externalItemManagementDTO;
    }

    //Update External Item Group Approval Status
    public ExternalItemManagementDTO updateExternalItemGroup(List<ExternalItemManagement> externalItemManagementList, String externalItemGroupApprovalStatus, String approval_remark) {
        ExternalItemManagementDTO externalItemManagementDTO = null;
        try {
            if (externalItemManagementList != null) {
                if (externalItemManagementList.size() > 0) {
                    Long inTransitQty = externalItemManagementList.get(0).getInTransitQty();
                    //InwardDto inwardDto = null;
                    externalItemManagementDTO = getEntityForUpdateAndDelete(externalItemManagementList.get(0).getId());
                    if (externalItemGroupApprovalStatus.equalsIgnoreCase(CommonConstants.APPROVE)) {
                        externalItemManagementDTO.setQty(inTransitQty);
                        externalItemManagementDTO.setUnusedQty(inTransitQty);
                        externalItemManagementDTO.setUsedQty(0L);
                        externalItemManagementDTO.setInTransitQty(0L);
                        externalItemManagementDTO.setRejectedQty(0L);
                        externalItemManagementDTO.setApprovalStatus(CommonConstants.APPROVE);
                        externalItemManagementDTO.setApprovalRemark(approval_remark);
                        super.updateEntity(externalItemManagementDTO);
                    } else if (externalItemGroupApprovalStatus.equalsIgnoreCase(CommonConstants.REJECTED)) {
                        externalItemManagementDTO.setQty(0L);
                        externalItemManagementDTO.setUnusedQty(0L);
                        externalItemManagementDTO.setUsedQty(0L);
                        externalItemManagementDTO.setInTransitQty(0L);
                        externalItemManagementDTO.setRejectedQty(inTransitQty);
                        externalItemManagementDTO.setApprovalStatus(CommonConstants.REJECTED);
                        externalItemManagementDTO.setApprovalRemark(approval_remark);
                        super.updateEntity(externalItemManagementDTO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return externalItemManagementDTO;
    }

    //Search
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getInwardList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getInwardList(String externalItemGroupNumber, PageRequest pageRequest) {
        try {
            String SUBMODULE = getModuleNameForLog() + " [getInwardList()] ";
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<ExternalItemManagement> finalPaginationList = null;
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<Long> resultPaginationList = new ArrayList<>();
                // Common method for find Service Area List Based on StaffId With Long
    //            ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Long> serviceAreaIds = serviceAreaService.getServiceAreaByStaffIdLong();
                List<ExternalItemManagement> inwardServiceAreaStaffList = new ArrayList<>();
                if (!serviceAreaIds.isEmpty()) {
                    inwardServiceAreaStaffList = externalItemManagementRepository.findAllByexternalItemGroupNumberContainingIgnoreCaseAndServiceAreaIdIdInAndIsDeletedIsFalseAndMvnoIdIn(externalItemGroupNumber,serviceAreaIds,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    inwardServiceAreaStaffList = externalItemManagementRepository.findAllByexternalItemGroupNumberContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(externalItemGroupNumber,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
                if (inwardServiceAreaStaffList != null) {
                    if (inwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }finalPaginationList = externalItemManagementRepository.findAllByIdIn(resultPaginationList, pageRequest);
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                finalPaginationList = externalItemManagementRepository.findAllByexternalItemGroupNumberContainingIgnoreCaseAndIsDeletedIsFalse(externalItemGroupNumber, pageRequest);
            }
            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getRandomenumber(String flag1, String flag2, String flag3,Integer mvnoId) {
        try {
            String flag = "";
            if (flag1 != null)
                flag += flag1;
            if (flag2 != null)
                flag += flag2;
            if (flag3 != null) {
                Integer count = externalItemManagementRepository.findTopByOrderByIdDesc(mvnoId);
                if (count == null || count==0)
                    flag += 1;
                else
                    flag += count + 1;
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<ExternalItemManagementDTO> getAllExtenralItemBaseOnStatus(Long ownerId,String ownershipType){
            List<ExternalItemManagementDTO> externalItemManagementDTOS=null;
             try {
                QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
                BooleanExpression booleanExpression = qExternalItemManagement.isNotNull().and(qExternalItemManagement.ownerId.eq(ownerId).and(qExternalItemManagement.ownershipType.equalsIgnoreCase(ownershipType)));
                List<ExternalItemManagement> externalItemManagementList = (List<ExternalItemManagement>) externalItemManagementRepository.findAll(booleanExpression);
                 externalItemManagementDTOS=externalItemManagementList.stream().map(externalItemManagement -> externalItemManagementMapper.domainToDTO(externalItemManagement, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            catch (Exception exception){
                 exception.printStackTrace();
                throw new RuntimeException(exception.getMessage());
            }
        return externalItemManagementDTOS;
    }


   public List<CommonResponceDto> getAllCustomerBasedOnServiceArea(List<Long> serviceAreaIds) {
       try {
           Map<Integer, String> map=new HashedMap();
           QCustomers qCustomers = QCustomers.customers;
           BooleanExpression booleanExpression = qCustomers.isNotNull();
           booleanExpression = booleanExpression.and(qCustomers.status.eq(CommonConstants.ACTIVE_STATUS)).and(qCustomers.isDeleted.eq(false)).and(qCustomers.servicearea.id.in(serviceAreaIds));
           List<Customers> customersList = (List<Customers>) customersRepository.findAll(booleanExpression);
           List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
           customersList.stream().forEach(r->{
               CommonResponceDto commonResponceDto = new CommonResponceDto();
               commonResponceDto.setId(r.getId().longValue());
               commonResponceDto.setName(r.getUsername());
               commonResponceDtos.add(commonResponceDto);
          });
           return commonResponceDtos;
       } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException(e);
       }
   }

    @Override
    public ExternalItemManagementDTO getEntityById(Long id) {
        try {
            ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(id).get();
            ExternalItemManagementDTO externalItemManagementDTO = getMapper().domainToDTO(externalItemManagement, new CycleAvoidingMappingContext());
            if(externalItemManagementDTO.getOwnershipType().equals("") || externalItemManagementDTO.getOwnershipType() == null) {
                externalItemManagementDTO.setOwnerName("");
            } else if(externalItemManagementDTO.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.CUSTOMER_OWNED)) {
                externalItemManagementDTO.setOwnerName(customersRepository.getOne(Math.toIntExact(externalItemManagementDTO.getOwnerId())).getFirstname());
            } else if (externalItemManagementDTO.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.PARTNER_OWNED)) {
                externalItemManagementDTO.setOwnerName(partnerRepository.getOne(Math.toIntExact(externalItemManagementDTO.getOwnerId())).getName());
            }
            return externalItemManagementDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public ExternalItemManagement getExternalItemById(long id){
        return externalItemManagementRepository.findById(id).get();
    }
}
