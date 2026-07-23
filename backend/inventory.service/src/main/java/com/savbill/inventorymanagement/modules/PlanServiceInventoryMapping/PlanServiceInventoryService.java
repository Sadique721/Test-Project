package com.savbill.inventorymanagement.modules.PlanServiceInventoryMapping;

import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanServiceInventoryService extends ExBaseAbstractService<PlanServiceInventoryMappingPojo, PlanServiceInventoryMapping, Long> {
    public PlanServiceInventoryService(PlanServiceInventoryRepository repository, PlanServiceInventoryMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PlanServiceInventoryService]";
    }

    @Autowired
    PlanServiceInventoryRepository planServiceInventoryRepository;

    public GenericDataDTO getPlanServiceInventoryByServiceId(Integer serviceId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<PlanServiceInventoryMapping> planServiceInventoryMappings = new ArrayList<>();
        List<ProductCategory> productCategoryList = new ArrayList<>();
        try {
            planServiceInventoryMappings = planServiceInventoryRepository.findAllByPlanService_Id(serviceId);
            if (!planServiceInventoryMappings.isEmpty()) {
                productCategoryList = planServiceInventoryMappings.stream().map(PlanServiceInventoryMapping::getProductCategory).collect(Collectors.toList());
                genericDataDTO.setDataList(productCategoryList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setDataList(productCategoryList);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }
}
