package com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.QProduct;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.ProductPlanGroupMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.ProductPlanGroupMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.QProductPlanGroupMapping;

import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductplanmappingService extends ExBaseAbstractService<Productplanmappingdto, Productplanmapping, Long> {

    @Autowired
    private ProductPlanMappingRepository productPlanMappingRepository;

    @Autowired
    private ProductPlanGroupMappingRepository productPlanGroupMappingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private InwardServiceImpl inwardService;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    Productplanmappingmapper productplanmappingmapper;

    private static final Logger logger = LoggerFactory.getLogger(ProductplanmappingService.class);

    public List<Productplanmapping> getallfromplan(Long id){
        List<Productplanmapping> list = new ArrayList<>();
        list = productPlanMappingRepository.getallfromplanid(id);
        return list;
    }
    public List<ProductCategory> getProductCategoryByPlanId(Long mappingId) {
        try {
            QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
            BooleanExpression booleanExpression = qProductplanmapping.id.eq(mappingId);
            List<Productplanmapping> productPlanMappings = IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression));
            List<ProductCategory> productCategory = new ArrayList<>();
            for (int i=0; i<productPlanMappings.size(); i++) {
                if (productPlanMappings.get(i).getProductCategoryId() != null) {
                    productCategory.add(productCategoryRepository.findById(productPlanMappings.get(i).getProductCategoryId()).get());
                }
            }
            return productCategory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Product> getProductByPlanId(Integer mappingId) {
        try {
            QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
            BooleanExpression booleanExpression = qProductplanmapping.id.eq(Long.valueOf(mappingId));
            List<Productplanmapping> productPlanMappings = IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression));
            if (productPlanMappings.get(0).getProductId() != null) {
                Product products = productRepository.findById(productPlanMappings.get(0).getProductId()).get();
                List<Product> productList = new ArrayList<>();
                productList.add(products);
                return productList;
            } else {
                QProduct qProduct = QProduct.product;
                BooleanExpression aBoolean = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.eq(CommonConstants.CUSTOMER_BIND));
                if (getMvnoIdFromCurrentStaff() != 1)
                    aBoolean = aBoolean.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                return (List<Product>) this.productRepository.findAll(aBoolean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public GenericDataDTO getProductPlanMappingByPlanId(Integer planId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Productplanmapping> productplanmappingList = new ArrayList<>();
        List<Productplanmappingdto> productplanmappingdtoList = new ArrayList<>();
        try {
            productplanmappingList = productPlanMappingRepository.getallfromplanid(Long.valueOf(planId));
            if (productplanmappingList.size() != 0) {
                productplanmappingList.forEach(productplanmapping -> {
                    if (productplanmapping.getPlanId() != null) {
                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(productplanmapping.getPlanId())).get();
                        productplanmapping.setPlanName(postpaidPlan.getName());
                    }
                    if (productplanmapping.getProductCategoryId() != null) {
                        ProductCategory productCategory = productCategoryRepository.findById(productplanmapping.getProductCategoryId()).get();
                        productplanmapping.setProductCategoryName(productCategory.getName());
                    }
                    if (productplanmapping.getProductId() != null) {
                        Product product = productRepository.findById(productplanmapping.getProductId()).get();
                        productplanmapping.setProductName(product.getName());
                    }
                });
                productplanmappingdtoList = productplanmappingList.stream().map(productplanmapping -> productplanmappingmapper.domainToDTO(productplanmapping, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                genericDataDTO.setDataList(productplanmappingdtoList);
            } else {
                genericDataDTO.setDataList(productplanmappingdtoList);
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
    public ProductplanmappingService(ProductPlanMappingRepository repository, Productplanmappingmapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductplanmappingService]";
    }
    //Delete Product Plan and Plan Group Mapping by Plan Group Id and Plan Id
    public List<ProductPlanGroupMapping> deleteProductPlanGroupMapping(Long planGroupId, Long planId) {
        List<ProductPlanGroupMapping> productPlanGroupMappingList = null;
        try {
            QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
            BooleanExpression booleanExpression = qProductPlanGroupMapping.planId.eq(planId).and(qProductPlanGroupMapping.planGroupId.eq(planGroupId));
            productPlanGroupMappingList = IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
            productPlanGroupMappingList.stream().forEach(productPlanGroupMapping -> {
                productPlanGroupMappingRepository.delete(productPlanGroupMapping);
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return productPlanGroupMappingList;
    }
}
