package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDateTime;

import com.savbill.salescrmsbss.entity.ProductPlanMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Productplanmappingdto {
	
	private Long id;
	
    private Long planId;
    
    private Long productCategoryId;
    
    private String product_type;
    
    private LocalDateTime createdate;
    
    private LocalDateTime updatedate;
    
    private String createdByName;
    
    private String lastModifiedByName;
    
    private Integer createdById;
    
    private Integer lastModifiedById;
    
    private Long productId;
    
    private Double revisedCharge;
    
    private String ownershipType;
    
    private  String name;
    
    private String productCategoryName;
    
    private String productName;
    
    private String planName;

    private Long productQuantity;


    public Productplanmappingdto(ProductPlanMapping productPlanMapping) {
        setId(productPlanMapping.getApigwProductPlanMappingId());
        if (productPlanMapping.getPostPaidPlan() != null) {
            setPlanId(productPlanMapping.getPostPaidPlan().getApiGatewayPlanId().longValue());
        }
        setPlanName(productPlanMapping.getPlanName());
        setProductCategoryId(productPlanMapping.getProductCategoryId());
        setProduct_type(productPlanMapping.getProduct_type());
        if (productPlanMapping.getProduct() != null)
            setProductId(productPlanMapping.getProduct().getApigwProductId());
        setCreatedate(productPlanMapping.getCreatedate());
        setUpdatedate(productPlanMapping.getUpdatedate());
        setLastModifiedById(productPlanMapping.getLastModifiedById());
        setLastModifiedByName(productPlanMapping.getLastModifiedByName());
        setCreatedByName(productPlanMapping.getCreatedByName());
        setCreatedById(productPlanMapping.getCreatedById());
        setRevisedCharge(productPlanMapping.getRevisedCharge());
        setOwnershipType(productPlanMapping.getOwnershipType());
        setName(productPlanMapping.getName());
        setProductCategoryName(productPlanMapping.getProductCategoryName());
        setProductName(productPlanMapping.getProductName());
        setProductQuantity(productPlanMapping.getQuantity());
    }
    
}
