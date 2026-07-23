package com.savbill.integrationsystem.rms.repository;

import com.savbill.integrationsystem.rms.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product,Long> {
    Product findByName(String productName);
}
