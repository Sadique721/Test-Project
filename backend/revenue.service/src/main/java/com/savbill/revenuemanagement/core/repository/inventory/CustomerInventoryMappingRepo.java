package com.savbill.revenuemanagement.core.repository.inventory;

import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInventoryMappingRepo extends JpaRepository<CustomerInventoryMapping, Long> {

    List<CustomerInventoryMapping> findAllByIdInAndCustomerId(List<Long> mappingIds, Long custId);

    @Query("SELECT t.id FROM CustomerInventoryMapping t WHERE t.customerId =:custId")
    List<Long> findAllByCustomerId(@Param(value = "custId") Long custId);

    @Query("SELECT t FROM CustomerInventoryMapping t WHERE t.id IN :ids")
    List<CustomerInventoryMapping> findByIds(@Param("ids") List<Long> ids);

}
