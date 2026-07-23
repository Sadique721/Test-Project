package com.savbill.revenuemanagement.productmanagement.Product_Plan_Mapping.service;

import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.core.service.ExBaseAbstractService;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.savbill.revenuemanagement.productmanagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import com.savbill.revenuemanagement.productmanagement.Product_Plan_Mapping.repository.ProductPlanMappingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductplanmappingService extends ExBaseAbstractService<Productplanmappingdto, Productplanmapping, Long> {

    @Autowired
    private ProductPlanMappingRepository productPlanMappingRepository;

//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private ProductCategoryRepository productCategoryRepository;
//
//    @Autowired
//    private InwardServiceImpl inwardService;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    private static final Logger logger = LoggerFactory.getLogger(ProductplanmappingService.class);

    public ProductplanmappingService(JpaRepository<Productplanmapping, Long> repository, IBaseMapper<Productplanmappingdto, Productplanmapping> mapper) {
        super(repository, mapper);
    }

    public List<Productplanmapping> getallfromplan(Long id){
        List<Productplanmapping> list = new ArrayList<>();
        list = productPlanMappingRepository.getallfromplanid(id);
        return list;
    }
//    public List<ProductCategory> getProductCategoryByPlanId(Long mappingId) {
//            QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
//            BooleanExpression booleanExpression = qProductplanmapping.id.eq(mappingId);
//            List<Productplanmapping> productPlanMappings = IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression));
//            List<ProductCategory> productCategory = new ArrayList<>();
//            for (int i=0; i<productPlanMappings.size(); i++) {
//                if (productPlanMappings.get(i).getProductCategoryId() != null) {
//                    productCategory.add(productCategoryRepository.findById(productPlanMappings.get(i).getProductCategoryId()).get());
//                }
//            }
//            return productCategory;
//    }
//
//    public List<Product> getProductByPlanId(Integer mappingId) {
//            QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
//            BooleanExpression booleanExpression = qProductplanmapping.id.eq(Long.valueOf(mappingId));
//            List<Productplanmapping> productPlanMappings = IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression));
//            if (productPlanMappings.get(0).getProductId() != null) {
//                Product products = productRepository.findById(productPlanMappings.get(0).getProductId()).get();
//                List<Product> productList = new ArrayList<>();
//                productList.add(products);
//                return productList;
//            } else {
//                QProduct qProduct = QProduct.product;
//                BooleanExpression aBoolean = qProduct.isNotNull().and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.eq(CommonConstants.CUSTOMER_BIND));
//                if (getMvnoIdFromCurrentStaff() != 1)
//                    aBoolean = aBoolean.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                return (List<Product>) this.productRepository.findAll(aBoolean);
//            }
//    }
//
//    public List<Productplanmapping> getProductPlanMappingByPlanId(Integer planId) {
//        List<Productplanmapping> productplanmappingList = productPlanMappingRepository.getallfromplanid(Long.valueOf(planId));
//        if (productplanmappingList.size() >0 || productplanmappingList != null) {
//            productplanmappingList.stream().forEach(productplanmapping -> {
//                if (productplanmapping.getPlanId() != null) {
//                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(productplanmapping.getPlanId())).get();
//                    productplanmapping.setPlanName(postpaidPlan.getName());
//                }
//                if (productplanmapping.getProductCategoryId() != null) {
//                    ProductCategory productCategory = productCategoryRepository.findById(productplanmapping.getProductCategoryId()).get();
//                    productplanmapping.setProductCategoryName(productCategory.getName());
//                }
//                if (productplanmapping.getProductId() != null) {
//                    Product product = productRepository.findById(productplanmapping.getProductId()).get();
//                    productplanmapping.setProductName(product.getName());
//                }
//            });
//        }
//        return productplanmappingList;
//    }
//    public ProductplanmappingService(ProductPlanMappingRepository repository, IBaseMapper<Productplanmappingdto, Productplanmapping> mapper) {
//        super(repository, mapper);
//    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductplanmappingService]";
    }
}
