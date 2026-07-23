package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByApigwProductId(Long productId);
}
