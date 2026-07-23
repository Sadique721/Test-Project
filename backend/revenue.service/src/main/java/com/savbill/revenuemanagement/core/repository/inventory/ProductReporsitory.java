package com.savbill.revenuemanagement.core.repository.inventory;

import com.savbill.revenuemanagement.core.entity.inventory.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReporsitory extends JpaRepository<Product, Long> {
}
