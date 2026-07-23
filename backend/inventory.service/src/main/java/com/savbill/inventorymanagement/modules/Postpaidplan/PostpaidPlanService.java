package com.savbill.inventorymanagement.modules.Postpaidplan;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.ChargeManagement.ChargeRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.ProductPlanMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.Productplanmapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.Productplanmappingdto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.Productplanmappingmapper;
import com.savbill.inventorymanagement.modules.PostpaidPlanCharge.PostpaidPlanCharge;
import com.savbill.inventorymanagement.modules.PostpaidPlanCharge.PostpaidPlanChargeRepo;
import com.savbill.inventorymanagement.modules.PostpaidPlanServiceAreaMapping.PostPaidPlanServiceAreaMapping;
import com.savbill.inventorymanagement.modules.PostpaidPlanServiceAreaMapping.PostPaidPlanServiceAreaMappingRepo;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SavePlanSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdatePlanSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostpaidPlanService extends ExBaseAbstractService<PostpaidPlanPojo, PostpaidPlan, Integer> {

    public PostpaidPlanService(PostpaidPlanRepo repository, PostpaidPlanMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PostpaidPlanService]";
    }

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    ProductPlanMappingRepository productPlanMappingRepository;
    
    @Autowired
    Productplanmappingmapper productplanmappingmapper;

    @Autowired
    PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;
    private static final Logger logger = Logger.getLogger(PostpaidPlanService.class);

//    @Transient
    public void savePostPaidPlanEntity(SavePlanSharedDataMessage message) throws Exception {
        try {
            PostpaidPlan postpaidPlan = new PostpaidPlan();
            postpaidPlan.setId(message.getId());
            postpaidPlan.setName(message.getName());
            postpaidPlan.setDisplayName(message.getDisplayName());
            postpaidPlan.setStatus(message.getStatus());
            postpaidPlan.setPlanStatus(message.getPlanStatus());
            postpaidPlan.setCreatedById(message.getCreatedById());
            postpaidPlan.setLastModifiedById(message.getLastModifiedById());
            postpaidPlan.setMvnoId(message.getMvnoId());
            postpaidPlan.setBuId(message.getBuId());
            postpaidPlan.setServiceId(message.getServiceId());
            postpaidPlan.setPlantype(message.getPlantype());
            postpaidPlan.setPlanGroup(message.getPlanGroup());
            postpaidPlan.setIsDelete(message.getIsDelete());
            postpaidPlan.setServiceAreaNameList(message.getServiceAreaNameList());
            postpaidPlanRepo.save(postpaidPlan);
            savePlanChargeList(message.getChargeList(), message.getId(), CommonConstants.OPERATION_ADD);
            if (message.getProductplanmappingList() != null) {
                saveProductPlanMapping(message.getProductplanmappingList(), Long.valueOf(message.getId()));
            }
            logger.info("Postpaid Plan details created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create postpaid plan details with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

//    @Transient
    public void updatePostPaidPlanEntity(UpdatePlanSharedDataMessage message) throws Exception {
        try {
            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(message.getId()).orElse(null);
            if (postpaidPlan != null) {
                postpaidPlan.setId(message.getId());
                postpaidPlan.setName(message.getName());
                postpaidPlan.setDisplayName(message.getDisplayName());
                postpaidPlan.setStatus(message.getStatus());
                postpaidPlan.setPlanStatus(message.getPlanStatus());
                postpaidPlan.setMvnoId(message.getMvnoId());
                postpaidPlan.setBuId(message.getBuId());
                postpaidPlan.setServiceId(message.getServiceId());
                postpaidPlan.setPlantype(message.getPlantype());
                postpaidPlan.setCreatedById(message.getCreatedById());
                postpaidPlan.setLastModifiedById(message.getLastModifiedById());
                postpaidPlan.setPlanGroup(message.getPlanGroup());
                postpaidPlan.setIsDelete(message.getIsDelete());
                if (message.getIsApprove() == false) {
                    postpaidPlan.setServiceAreaNameList(message.getServiceAreaNameList());
                }
                postpaidPlanRepo.save(postpaidPlan);
                savePlanChargeList(message.getChargeList(), message.getId(), CommonConstants.OPERATION_UPDATE);
                if (message.getProductplanmappingList() != null) {
                    if (message.getIsApprove() == false) {
                        saveProductPlanMapping(message.getProductplanmappingList(), Long.valueOf(message.getId()));
                    }
                }
                logger.info("Postpaid Plan details updated successfully with name " + message.getName());
            } else {
                PostpaidPlan postpaidPlan1 = new PostpaidPlan();
                postpaidPlan1.setId(message.getId());
                postpaidPlan1.setName(message.getName());
                postpaidPlan1.setDisplayName(message.getDisplayName());
                postpaidPlan1.setStatus(message.getStatus());
                postpaidPlan1.setPlanStatus(message.getPlanStatus());
                postpaidPlan1.setCreatedById(message.getCreatedById());
                postpaidPlan1.setLastModifiedById(message.getLastModifiedById());
                postpaidPlan1.setMvnoId(message.getMvnoId());
                postpaidPlan1.setServiceId(message.getServiceId());
                postpaidPlan1.setPlantype(message.getPlantype());
                postpaidPlan.setBuId(message.getBuId());
                postpaidPlan1.setPlanGroup(message.getPlanGroup());
                postpaidPlan1.setIsDelete(message.getIsDelete());
                postpaidPlan1.setServiceAreaNameList(message.getServiceAreaNameList());
                postpaidPlanRepo.save(postpaidPlan1);
                savePlanChargeList(message.getChargeList(), message.getId(), CommonConstants.OPERATION_ADD);
                if (message.getProductplanmappingList() != null) {
                    saveProductPlanMapping(message.getProductplanmappingList(), Long.valueOf(message.getId()));
                }
                logger.info("Postpaid Plan details updated successfully with name " + message.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update postpaid plan details with name " + message.getName() + " , Error: " + e.getMessage());
        }
    }

    public String getRandomNumber(String flag1, String flag2, String flag3) {
        String flag = "";
        if (flag1 != null) {
            flag += flag1;
        }
        if (flag2 != null) {
            flag += flag2;
        }
        if (flag3 != null) {
            Productplanmapping productplanmapping = productPlanMappingRepository.findTopByOrderByIdDesc();
            if (productplanmapping == null) {
                flag += 1;
            } else {
                flag += productplanmapping.getId() + 1;
            }
        }
        return flag;
    }

//    @Transient
    public void saveProductPlanMapping(List<Productplanmappingdto> productplanmappingdtoList, Long planId) throws Exception{
        try {
            List<Productplanmapping> productplanmappingList = productPlanMappingRepository.findAllByPlanId(planId);
            if(productplanmappingdtoList.size()!=0) {
                List<Long> idsList=productplanmappingList.stream().map(Productplanmapping::getId).collect(Collectors.toList());
                List<Long> productPlanMappingIdList=productplanmappingdtoList.stream().map(Productplanmappingdto::getId).collect(Collectors.toList());
                if(productplanmappingList.size()!=0) {
                    ArrayList<Long> deletedItems = new ArrayList<Long>(idsList);
                    deletedItems.removeAll(productPlanMappingIdList);
                    deletedItems.forEach(r -> {
                        productPlanMappingRepository.deleteById(r);
                    });
                } else {
                    productPlanMappingRepository.deleteAll(productplanmappingList);
                }
            } else {
                productPlanMappingRepository.deleteAll(productplanmappingList);
            }
            for (Productplanmappingdto dto : productplanmappingdtoList) {
                dto.setPlanId(planId);
                Product product = productRepository.findById(dto.getProductId()).orElse(null);
                ProductCategory productCategory = productCategoryRepository.findById(dto.getProductCategoryId()).orElse(null);
                if (dto.getName() == null) {
                    if (product != null) {
                        dto.setName(getRandomNumber(product.getName(), "-", ""));
                    } else if (productCategory != null) {
                        dto.setName(getRandomNumber(productCategory.getName(), "-", ""));
                    }
                    productPlanMappingRepository.save(productplanmappingmapper.dtoToDomain(dto, new CycleAvoidingMappingContext()));
                }
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

//    @Transient
    public void savePlanChargeList(List<PostpaidPlanCharge> chargeList, Integer planId, Integer operation) {
        try {
            List<PostpaidPlanCharge> postpaidPlanCharges = postpaidPlanChargeRepo.findAllByPlanId(planId);
            if(postpaidPlanCharges.size()!=0) {
                for (PostpaidPlanCharge chargeLists : chargeList) {
                    for (PostpaidPlanCharge item : postpaidPlanCharges) {
                        if (!chargeLists.getCharge().getId().equals(item.getCharge().getId())) {
                            item.setIsDelete(true);
                            postpaidPlanChargeRepo.save(item);
                        }
                    }
                }
            }
            PostpaidPlan postpaidPlan = new PostpaidPlan();
            postpaidPlan.setId(planId);
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                for (PostpaidPlanCharge item : chargeList) {
                    item.setPlan(postpaidPlan);
                    postpaidPlanChargeRepo.save(item);
                }
                logger.info("Postpaid Plan Charge details created successfully with plan id " + planId);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                for (PostpaidPlanCharge chargeLists : chargeList) {
                    for (PostpaidPlanCharge item : postpaidPlanCharges) {
                        if (!chargeLists.getCharge().getId().equals(item.getCharge().getId())) {
                            chargeLists.setPlan(postpaidPlan);
                            postpaidPlanChargeRepo.save(chargeLists);
                        }
                    }
                }
                logger.info("Postpaid Plan Charge details updated successfully with plan id " + planId);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public void assignPlanToServiceArea(List<PostPaidPlanServiceAreaMapping> mappingList) {
        try {
            if (mappingList == null || mappingList.isEmpty()) {
                logger.warn("No plan-service area mappings received to assign.");
            }
            LocalDateTime now = LocalDateTime.now();
            for (PostPaidPlanServiceAreaMapping mapping : mappingList) {
                if (mapping.getCreatedate() == null) {
                    mapping.setCreatedate(now);
                }
            }
            List<PostPaidPlanServiceAreaMapping> savedMappings = postPaidPlanServiceAreaMappingRepo.saveAll(mappingList);
            logger.info("Saved " + savedMappings.size() + " plan-service area mappings received via Kafka.");
        } catch (CustomValidationException e) {
            logger.error("Unable to save plan-service area mappings received via Kafka. , Error: " + e.getMessage());
        }
    }
}
