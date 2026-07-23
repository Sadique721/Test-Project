package com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.InventorySpecification;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.InventorySpecificationRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.Inward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductParameterDefaultValueMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductParameterMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.acl.model.ProductParameterDefaultValueMappingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpecificationParametersService extends ExBaseAbstractService<SpecificationParametersDTO, SpecificationParameters, Long> {
    @Autowired
    SpecificationParametersMapper specificationParametersMapper;

    @Autowired
    SpecificatioParametersRepo specificatioParametersRepo;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductParameterMappingRepo productParameterMappingRepo;
    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    InwardRepository inwardRepository;

    @Autowired
    InventorySpecificationRepo inventorySpecificationRepo;

    public SpecificationParametersService(SpecificatioParametersRepo repository, SpecificationParametersMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[SpecificationParametersService]";
    }

    public GenericDataDTO saveEntity(ProductCategory productCategory, List<SpecificationParametersDTO> specificationParametersDTOList) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<SpecificationParameters> specificationParametersList = new ArrayList<>();
        try {
                for (SpecificationParametersDTO itemDTO : specificationParametersDTOList) {
                    SpecificationParameters specificationParameters = new SpecificationParameters();
                    specificationParameters.setParamName(itemDTO.getParamName());
                    specificationParameters.setProductCategory(productCategory);
                    specificationParameters.setIsMandatory(itemDTO.getIsMandatory());
                    specificationParameters.setMvnoId(productCategory.getMvnoId());
                    if(itemDTO.getIsMultiValueParam()!=null)
                        specificationParameters.setIsMultiValueParam(itemDTO.getIsMultiValueParam());
                    else
                        specificationParameters.setIsMultiValueParam(false);

                    if(itemDTO.getIsMultiValueParam()!=null && itemDTO.getIsMultiValueParam().equals(true))
                    {
                        String multiValues=itemDTO.getParamMultiValues().stream().collect(Collectors.joining(","));
                        specificationParameters.setParamValues(multiValues);
                    }
                    specificationParametersList.add(specificationParameters);
                }

            specificationParametersList = specificatioParametersRepo.saveAll(specificationParametersList);
            genericDataDTO.setDataList(specificationParametersList.stream().map(data -> specificationParametersMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }

    public GenericDataDTO updateEntity(ProductCategory productCategory, List<SpecificationParametersDTO> specificationParametersDTOList,ProductCategoryDto entity) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<SpecificationParameters> modifiedSpecificParameters = new ArrayList<>();
        try {
            for (SpecificationParametersDTO itemDTO : specificationParametersDTOList) {
                if (itemDTO.getId() != null) {
                    SpecificationParameters specificationParameters = specificatioParametersRepo.findById(itemDTO.getId()).orElse(null);
                    specificationParameters.setParamName(itemDTO.getParamName());
                    specificationParameters.setProductCategory(productCategory);
                    specificationParameters.setIsMandatory(itemDTO.getIsMandatory());
                    specificationParameters.setMvnoId(productCategory.getMvnoId());

                    if(itemDTO.getIsMultiValueParam()!=null)
                        specificationParameters.setIsMultiValueParam(itemDTO.getIsMultiValueParam());
                    else
                        specificationParameters.setIsMultiValueParam(false);

                    if(itemDTO.getIsMultiValueParam()!=null && itemDTO.getIsMultiValueParam().equals(true))
                    {
                        String multiValues=itemDTO.getParamMultiValues().stream().collect(Collectors.joining(","));
                        specificationParameters.setParamValues(multiValues);
                    }
                    modifiedSpecificParameters.add(specificationParameters);
                } else {
                    SpecificationParameters newParameters = new SpecificationParameters();
                    newParameters.setParamName(itemDTO.getParamName());
                    newParameters.setProductCategory(productCategory);
                    newParameters.setIsMandatory(itemDTO.getIsMandatory());
                    newParameters.setMvnoId(productCategory.getMvnoId());

                    if(itemDTO.getIsMultiValueParam()!=null)
                        newParameters.setIsMultiValueParam(itemDTO.getIsMultiValueParam());
                    else
                        newParameters.setIsMultiValueParam(false);

                    if(itemDTO.getIsMultiValueParam()!=null && itemDTO.getIsMultiValueParam().equals(true))
                    {
                        String multiValues=itemDTO.getParamMultiValues().stream().collect(Collectors.joining(","));
                        newParameters.setParamValues(multiValues);
                    }
                    modifiedSpecificParameters.add(newParameters);
                }
            }

            modifiedSpecificParameters = specificatioParametersRepo.saveAll(modifiedSpecificParameters);

            if(entity!=null && entity.getIsUpgradeWithExistingProductItem()!=null && entity.getIsUpgradeWithExistingProductItem())
                updateExistingProductAndInventoryItems(entity,productCategory,modifiedSpecificParameters);

            genericDataDTO.setDataList(modifiedSpecificParameters.stream().map(data -> specificationParametersMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }


    public void updateExistingProductAndInventoryItems(ProductCategoryDto entity,ProductCategory productCategory, List<SpecificationParameters> specificationParametersList)
    {
        try {
            if(entity.getSpecificationParametersDTOList()!=null && !entity.getSpecificationParametersDTOList().isEmpty())
            {
                List<Product> products=productRepository.findAllByStatusAndProductCategoryAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS,productCategory);

                List<SpecificationParametersDTO> dtos=entity.getSpecificationParametersDTOList().stream().filter(x->x.getId()==null).collect(Collectors.toList());
                List<String> specNameList=dtos.stream().filter(x->x.getId()==null).map(x->x.getParamName()).collect(Collectors.toList());
                specificationParametersList=specificationParametersList.stream().filter(x->specNameList.contains(x.getParamName())).collect(Collectors.toList());
                specificationParametersList.forEach(x->{
                    dtos.forEach(y->{
                        if(x.getParamName().equalsIgnoreCase(y.getParamName()))
                            x.setNewParamDefaultValue(y.getNewParamDefaultValue());
                    });
                });

                if(products!=null && !products.isEmpty())
                {
                    List<SpecificationParameters> finalSpecificationParametersList = specificationParametersList;
                    products.stream().forEach(product -> {
                        finalSpecificationParametersList.stream().forEach(param->{
                            ProductParameterDefaultValueMapping mapping=new ProductParameterDefaultValueMapping();
                            mapping.setProductId(product.getId());
                            mapping.setParameterId(param.getId());
                            mapping.setDefaultValue(param.getNewParamDefaultValue());
                            productParameterMappingRepo.save(mapping);
                        });
                    });
                }

                List<Inward> inwardIds=inwardRepository.findAllByApprovalStatusAndOutwardIdIsNullAndProductIdInAndIsDeletedFalse(CommonConstants.APPROVE,products);
                List<SpecificationParameters> finalSpecificationParametersList1 = specificationParametersList;
                inwardIds.stream().forEach(inward -> {
                    finalSpecificationParametersList1.stream().forEach(param->{
                        InventorySpecification inventorySpecification=new InventorySpecification();
                        inventorySpecification.setDeleteFlag(false);
                        inventorySpecification.setSpecificationParameters(param);
                        inventorySpecification.setInward(inward);
                        inventorySpecification.setInvenSpecId(inward.getId());
                        inventorySpecification.setParamValue(param.getNewParamDefaultValue());
                        inventorySpecification.setCreatedate(LocalDateTime.now());
                        inventorySpecification.setCreatedById(getLoggedInUserId());
                        inventorySpecification.setLastModifiedById(getLoggedInUserId());
                        inventorySpecification.setCreatedByName(getLoggedInUser().getFirstName());
                        inventorySpecification.setLastModifiedByName(getLoggedInUser().getFirstName());
                        inventorySpecificationRepo.save(inventorySpecification);
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public GenericDataDTO getSpecificParametersByid(Long product_id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            Product product = productRepository.findById(product_id).orElse(null);
            ProductCategory productCategory = product.getProductCategory();
            List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(productCategory.getId());
            if (specificationParameters != null) {
                List<SpecificationParametersDTO> dtos=specificationParameters.stream().map(data -> specificationParametersMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());

                dtos.forEach(x->{
                    String defaultValue=productParameterMappingRepo.getByProductIdAndParamId(product.getId(),x.getId());
                    x.setDefaultValue(defaultValue);
                    if(x.getIsMultiValueParam()!=null && x.getIsMultiValueParam().equals(true))
                        x.setIsMultiValueParam(true);
                    else
                        x.setIsMultiValueParam(false);

                    if(x.getIsMultiValueParam()!=null && x.getIsMultiValueParam().equals(true) && x.getParamValues()!=null && !x.getParamValues().isEmpty())
                        x.setParamMultiValues(Arrays.asList(x.getParamValues().split(",",-1)));
                });
                genericDataDTO.setDataList(dtos);

                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }

    public GenericDataDTO getSpecificParametersByCustId(Integer custId, String connectionNo, List<Long> invMappIds) {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            List<ProductParameterDefaultValueMappingDTO> parameters = new ArrayList<>();
            List<CustomerInventoryMapping> customerInventoryMapping = customerInventoryMappingRepo.findAllByIdIn(invMappIds);//customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(connectionNo, custId);
            if(!CollectionUtils.isEmpty(customerInventoryMapping)) {
                List<Product> products = customerInventoryMapping.stream().map(CustomerInventoryMapping::getProduct).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(products)) {
                    for(Product product: products) {
                        Set<ProductCategory> productCats = products.stream().map(Product::getProductCategory).collect(Collectors.toSet());
                        for (ProductCategory productCategory: productCats) {
                            List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(productCategory.getId());
                            for (SpecificationParameters parameters1: specificationParameters) {
                                List<ProductParameterDefaultValueMapping> productParameterDefaultValueMappings = productParameterMappingRepo.getProductMappingByProductIdAndParamId(product.getId(), parameters1.getId());
                                parameters.addAll(getProductParams(productParameterDefaultValueMappings, parameters1));
                            }
                        }
                    }
                }
            }
            if(!CollectionUtils.isEmpty(parameters)) {
                genericDataDTO.setDataList(parameters);
                genericDataDTO.setResponseCode(200);
                genericDataDTO.setResponseMessage("Success!!");
            } else {
                genericDataDTO.setDataList(null);
                genericDataDTO.setResponseCode(404);
                genericDataDTO.setResponseMessage("No Data found!!");
            }
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<ProductParameterDefaultValueMappingDTO> getProductParams(List<ProductParameterDefaultValueMapping> productParameterDefaultValueMappings, SpecificationParameters parameters) {
        return productParameterDefaultValueMappings.stream().map(productParameterDefaultValueMapping -> new ProductParameterDefaultValueMappingDTO(parameters, productParameterDefaultValueMapping.getDefaultValue())).collect(Collectors.toList());
    }

    public GenericDataDTO getSpecificParametersByProductCategoryId(Long product_category_id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            ProductCategory productCategory = productCategoryRepository.findById(product_category_id).orElse(null);
            List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(productCategory.getId());

            List<SpecificationParametersDTO> dtos=specificationParameters.stream().map(data -> specificationParametersMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());

            dtos.forEach(x->{
                if(x.getIsMultiValueParam()!=null && x.getIsMultiValueParam().equals(true))
                    x.setIsMultiValueParam(true);
                else
                    x.setIsMultiValueParam(false);

                if(x.getIsMultiValueParam()!=null && x.getIsMultiValueParam().equals(true) && x.getParamValues()!=null && !x.getParamValues().isEmpty())
                    x.setParamMultiValues(Arrays.asList(x.getParamValues().split(",",-1)));
            });

            if (specificationParameters != null) {
                genericDataDTO.setDataList(dtos);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }



    public List<SpecificationParametersDTO> getDTOByPCID(Long id) {
        try {
            List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(id);
            List<SpecificationParametersDTO> specificationParametersDTOS = new ArrayList<>();
            if (!specificationParameters.isEmpty()) {
                specificationParametersDTOS = specificationParameters.stream().map(specificationParameters1 -> specificationParametersMapper.domainToDTO(specificationParameters1, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            if(specificationParametersDTOS!=null && !specificationParametersDTOS.isEmpty())
            {
                for(SpecificationParametersDTO parametersDTO:specificationParametersDTOS)
                {
                    if(parametersDTO.getIsMultiValueParam()!=null && parametersDTO.getIsMultiValueParam())
                        parametersDTO.setParamMultiValues(Arrays.asList(parametersDTO.getParamValues().split(",", -1)));
                }
            }
            return specificationParametersDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}


