package com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping;

import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroup;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroupRepository;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductPlanGroupMappingService extends ExBaseAbstractService<ProductPlanGroupMappingDto, ProductPlanGroupMapping, Long> {

    public ProductPlanGroupMappingService(ProductPlanGroupMappingRepository repository, ProductPlanGroupMappingMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductPlanGroupMappingService]";
    }

    @Autowired
    ProductPlanGroupMappingRepository productPlanGroupMappingRepository;
    @Autowired
    PlanGroupRepository planGroupRepository;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;
    @Autowired
    ProductPlanGroupMappingMapper productPlanGroupMappingMapper;
    public GenericDataDTO getProductPlanGroupMappingDetails(Long planGroupId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductPlanGroupMappingDto> productPlanGroupMappingDtos = new ArrayList<>();
        List<ProductPlanGroupMapping> productPlanGroupMappings = new ArrayList<>();
        try {
            productPlanGroupMappings = productPlanGroupMappingRepository.findAllByPlanGroupId(planGroupId);
            if (!productPlanGroupMappings.isEmpty()) {
                productPlanGroupMappings.forEach(productPlanGroupMapping -> {
                        if (productPlanGroupMapping.getPlanGroupId() != null) {
                            Integer plangroupId = productPlanGroupMapping.getPlanGroupId().intValue();
                            productPlanGroupMapping.setPlanGroupName(planGroupRepository.findById(plangroupId).map(PlanGroup::getPlanGroupName).orElse(null));
                        }
                        if (productPlanGroupMapping.getPlanId() != null) {
                            Integer planId = productPlanGroupMapping.getPlanId().intValue();
                            productPlanGroupMapping.setPlanName(postpaidPlanRepo.findById(planId).map(PostpaidPlan::getName).orElse(null));
                        }
                        if (productPlanGroupMapping.getProductId() != null) {
                            productPlanGroupMapping.setProductName(productRepository.findById(productPlanGroupMapping.getProductId())
                                    .map(Product::getName).orElse(null));
                        }
                        if (productPlanGroupMapping.getProductCategoryId() != null) {
                            productPlanGroupMapping.setProductCategoryName(productCategoryRepository.findById(productPlanGroupMapping.getProductCategoryId())
                                    .map(ProductCategory::getName).orElse(null));
                        }
                    }
                );
                productPlanGroupMappingDtos = productPlanGroupMappings.stream()
                        .map(productPlanGroupMapping -> productPlanGroupMappingMapper.domainToDTO(productPlanGroupMapping, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                genericDataDTO.setDataList(productPlanGroupMappingDtos);
            } else {
                genericDataDTO.setDataList(productPlanGroupMappingDtos);
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }
}
