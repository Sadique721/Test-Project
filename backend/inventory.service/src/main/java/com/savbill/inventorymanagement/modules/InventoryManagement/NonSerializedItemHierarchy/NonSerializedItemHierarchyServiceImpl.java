package com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItemHierarchy;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.NonSerializedItem;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.NonSerializedItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.QNonSerializedItem;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NonSerializedItemHierarchyServiceImpl extends ExBaseAbstractService<NonSerializedItemHierarchyDto, NonSerializedItemHierarchy, Long> {

    @Autowired
    NonSerializedItemRepository nonSerializedItemRepository;

    @Autowired
    NonSerializedItemHierarchyRepository nonSerializedItemHierarchyRepository;

    @Autowired
    ProductRepository productRepository;

    public NonSerializedItemHierarchyServiceImpl(NonSerializedItemHierarchyRepository repository, IBaseMapper<NonSerializedItemHierarchyDto, NonSerializedItemHierarchy> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[NonSerializedItemHierarchyServiceImpl]";
    }

    public void updateNonSerializedItemHierarchy(Long inwardId, Long productId) {
        try {

            QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
            BooleanExpression itemFilter = qNonSerializedItem.isDeleted.eq(false)
                    .and(qNonSerializedItem.currentInwardId.eq(inwardId));
            List<NonSerializedItem> nonSerializedItems = IterableUtils.toList(nonSerializedItemRepository.findAll(itemFilter));
            if (nonSerializedItems.isEmpty()) return; // Exit early if no items found
            // Fetch hierarchy records in a single query
            QNonSerializedItemHierarchy qHierarchy = QNonSerializedItemHierarchy.nonSerializedItemHierarchy;
            BooleanExpression hierarchyFilter = qHierarchy.isDeleted.eq(false)
                    .and(qHierarchy.childItemId.in(nonSerializedItems.stream()
                            .map(NonSerializedItem::getId)
                            .collect(Collectors.toList())));
            List<NonSerializedItemHierarchy> hierarchyItems = IterableUtils.toList(nonSerializedItemHierarchyRepository.findAll(hierarchyFilter));
            if (!hierarchyItems.isEmpty()) {
                hierarchyItems.forEach(h -> h.setIsDeleted(true));
                nonSerializedItemHierarchyRepository.saveAll(hierarchyItems); // Batch update
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating NonSerializedItemHierarchy", e);
        }
    }
}

