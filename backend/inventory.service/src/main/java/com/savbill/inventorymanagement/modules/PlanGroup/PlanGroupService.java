package com.savbill.inventorymanagement.modules.PlanGroup;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.ProductPlanGroupMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.ProductPlanGroupMappingRepository;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SavePlanGroupSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdatePlanGroupSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanGroupService extends ExBaseAbstractService<PlanGroupDTO, PlanGroup, Integer> {

    public PlanGroupService(PlanGroupRepository repository, PlanGroupMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PlanGroupService]";
    }
    private static final Logger logger = Logger.getLogger(PlanGroupService.class);

    @Autowired
    PlanGroupRepository planGroupRepository;

    @Autowired
    ProductPlanGroupMappingRepository productPlanGroupMappingRepository;

    public void savePlanGroupEntity(SavePlanGroupSharedDataMessage message) throws Exception {
        try {
            PlanGroup planGroup = new PlanGroup();
            planGroup.setPlanGroupId(message.getPlanGroupId());
            planGroup.setPlanGroupName(message.getPlanGroupName());
            planGroup.setStatus(message.getStatus());
            planGroup.setMvnoId(message.getMvnoId());
            planGroup.setCreatedById(message.getCreatedById());
            planGroup.setLastModifiedById(message.getLastModifiedById());
            planGroup.setPlanGroupType(message.getPlanGroupType());
            planGroup.setPlanMode(message.getPlanMode());
            planGroup.setPlantype(message.getPlantype());
            planGroup.setIsDelete(message.getIsDelete());
            planGroup.setPlanMappingList(message.getPlanMappingList());
            planGroup.setBuId(message.getBuId());
            planGroup.setCategory(message.getCategory());
            planGroup.setServicearea(message.getServicearea());
            planGroup.setProductPlanGroupMappingList(message.getProductPlanGroupMappingList());
            planGroupRepository.save(planGroup);
            logger.info("Country details created successfully with name " + message.getPlanGroupName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create country details with name " + message.getPlanGroupName() + " , Error: " + e.getMessage());
        }
    }

    public void updatePlanGroupEntity(UpdatePlanGroupSharedDataMessage message) throws Exception {
        try {
            PlanGroup planGroup = planGroupRepository.findById(message.getPlanGroupId()).orElse(null);
            if (planGroup != null) {
                planGroup.setPlanGroupId(message.getPlanGroupId());
                planGroup.setPlanGroupName(message.getPlanGroupName());
                planGroup.setStatus(message.getStatus());
                planGroup.setMvnoId(message.getMvnoId());
                planGroup.setPlanGroupType(message.getPlanGroupType());
                planGroup.setPlanMode(message.getPlanMode());
                planGroup.setPlantype(message.getPlantype());
                planGroup.setCreatedById(message.getCreatedById());
                planGroup.setLastModifiedById(message.getLastModifiedById());
                planGroup.setIsDelete(message.getIsDelete());
                planGroup.setPlanMappingList(message.getPlanMappingList());
                planGroup.setBuId(message.getBuId());
                planGroup.setCategory(message.getCategory());
                planGroup.setServicearea(message.getServicearea());
                if (message.getStatus().equals("NewActivation")) {
                    List<ProductPlanGroupMapping> productPlanGroupMappings = updateProductPlanGroupMapping(message.getProductPlanGroupMappingList(), Long.valueOf(message.getPlanGroupId()));
                    planGroup.setProductPlanGroupMappingList(productPlanGroupMappings);
                }
                planGroupRepository.save(planGroup);
                logger.info("Country details created successfully with name " + message.getPlanGroupName());
            } else {
                PlanGroup planGroup1 = new PlanGroup();
                planGroup1.setPlanGroupId(message.getPlanGroupId());
                planGroup1.setPlanGroupName(message.getPlanGroupName());
                planGroup1.setStatus(message.getStatus());
                planGroup1.setMvnoId(message.getMvnoId());
                planGroup1.setCreatedById(message.getCreatedById());
                planGroup1.setLastModifiedById(message.getLastModifiedById());
                planGroup1.setPlanGroupType(message.getPlanGroupType());
                planGroup1.setPlanMode(message.getPlanMode());
                planGroup1.setPlantype(message.getPlantype());
                planGroup1.setIsDelete(message.getIsDelete());
                planGroup1.setPlanMappingList(message.getPlanMappingList());
                planGroup1.setBuId(message.getBuId());
                planGroup1.setCategory(message.getCategory());
                planGroup1.setServicearea(message.getServicearea());
                planGroup1.setProductPlanGroupMappingList(message.getProductPlanGroupMappingList());
                planGroupRepository.save(planGroup1);
                logger.info("Country details created successfully with name " + message.getPlanGroupName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to create country details with name " + message.getPlanGroupName() + " , Error: " + e.getMessage());
        }
    }
    public List<ProductPlanGroupMapping> updateProductPlanGroupMapping(List<ProductPlanGroupMapping> productPlanGroupMappingList, Long planGroupId) {
        List<ProductPlanGroupMapping> finalProductPlanGroupMapping = new ArrayList<>();
        try{
            List<ProductPlanGroupMapping> productPlanGroupMappings = productPlanGroupMappingRepository.findAllByPlanGroupId(planGroupId);
            if (!productPlanGroupMappingList.isEmpty()) {
                List<Long> idsList = productPlanGroupMappings.stream().map(ProductPlanGroupMapping::getId).collect(Collectors.toList());
                if (!productPlanGroupMappings.isEmpty()) {
                    for (Long ids : idsList) {
                        productPlanGroupMappingRepository.deleteById(ids);
                    }
                } else {
                    productPlanGroupMappingRepository.deleteAll(productPlanGroupMappings);
                }
            } else {
                productPlanGroupMappingRepository.deleteAll(productPlanGroupMappings);
            }
            for (ProductPlanGroupMapping mapping : productPlanGroupMappingList) {
                ProductPlanGroupMapping productPlanGroupMapping = new ProductPlanGroupMapping();
                productPlanGroupMapping.setProductId(mapping.getProductId());
                productPlanGroupMapping.setPlanGroupId(mapping.getPlanGroupId());
                productPlanGroupMapping.setProductCategoryId(mapping.getProductCategoryId());
                productPlanGroupMapping.setName(mapping.getName());
                productPlanGroupMapping.setPlanId(mapping.getPlanId());
                productPlanGroupMapping.setRevisedCharge(mapping.getRevisedCharge());
                productPlanGroupMapping.setOwnershipType(mapping.getOwnershipType());
                productPlanGroupMapping.setProduct_type(mapping.getProduct_type());
                finalProductPlanGroupMapping.add(productPlanGroupMapping);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return finalProductPlanGroupMapping;
    }
}
