package com.savbill.cpm.modules.InventoryManagement.productCategory;

import com.savbill.cpm.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public interface ProductCategoryMapper  extends IBaseMapper<ProductCategoryDto, ProductCategory> {
}
