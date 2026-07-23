package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class InventorySpecificationHistoryService extends ExBaseAbstractService<InventorySpecificationHistoryDto, InventorySpecificationHistory, Long> {
    public InventorySpecificationHistoryService(JpaRepository<InventorySpecificationHistory, Long> repository, IBaseMapper<InventorySpecificationHistoryDto, InventorySpecificationHistory> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InventorySpecificationHistoryService]";
    }
}
