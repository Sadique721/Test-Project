package com.savbill.integrationsystem.rms.repository;

import com.savbill.integrationsystem.rms.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepo extends JpaRepository<ProductCategory,Long> {
    ProductCategory findByName(String name);
}
