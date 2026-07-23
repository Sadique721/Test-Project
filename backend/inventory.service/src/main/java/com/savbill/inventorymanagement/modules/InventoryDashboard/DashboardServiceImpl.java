package com.savbill.inventorymanagement.modules.InventoryDashboard;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwner;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouse;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouseManagmentServiceAreamappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WarehouseManagementRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl {

    @Autowired
    StaffUserService staffUserService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductOwnerRepository productOwnerRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    ServiceAreaService serviceAreaService;
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;
    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;

    public GenericDataDTO getProductQtyByStaff(PaginationRequestDTO paginationRequestDTO, Integer mvnoId) {
        PageRequest pageRequest = staffUserService.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long ownerId = (long) staffUserService.getLoggedInUserId();
        String ownerType = CommonConstants.STAFF;
        List<Long> productIds;
        if (mvnoId == 1) {
            productIds = productRepository.findAllByIsDeletedIsFalse().stream().map(Product::getId).collect(Collectors.toList());
        } else {
            productIds = productRepository.findAllByIsDeletedIsFalseAndMvnoId(mvnoId).stream().map(Product::getId).collect(Collectors.toList());
        }
        Page<ProductOwner> paginationList = productOwnerRepository.findAllByProductIdInAndOwnerIdAndOwnerType(productIds, ownerId, ownerType, pageRequest);
        paginationList.stream().forEach(r -> {
            Product product = productRepository.findById(r.getProductId()).get();
            r.setProductName(product.getName());
        });
        return getGenericDataDTO(genericDataDTO, paginationList);
    }

    public GenericDataDTO getProductQtyByWarehouse(PaginationRequestDTO paginationRequestDTO, Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = staffUserService.generatePageRequest(
                paginationRequestDTO.getPage(),
                paginationRequestDTO.getPageSize(),
                "createdate",
                CommonConstants.SORT_ORDER_DESC
        );
        List<Long> warehouseIds;
        List<Long> productIds;
        /** Fetch Warehouse IDs */
        if (mvnoId == 1) {
            warehouseIds = warehouseManagementRepository.findFilterdWarehouseIds();
        } else {
            List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
            if (!serviceAreaIds.isEmpty()) {
                List<Long> warehouseMapIds = wareHouseManagmentServiceAreamappingRepo.findWarehouseIdsBySAIds(serviceAreaIds);
                warehouseIds = warehouseManagementRepository.findWarehouseIdsAndMvnoIdsByWarehouseMappIds(warehouseMapIds, Arrays.asList(mvnoId, 1));
            } else {
                warehouseIds = warehouseManagementRepository.findFilterdWarehouseIdsByMvnoIds(Arrays.asList(mvnoId, 1));
            }
        }
        /** return if no warehouses */
        if (warehouseIds == null || warehouseIds.isEmpty()) {
            return getGenericDataDTO(genericDataDTO, Page.empty());
        }
        /** Fetch Product IDs */
        if (mvnoId == 1) {
            productIds = productRepository.findAllByIsDeletedIsFalse().stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());
        } else {
            productIds = productRepository.findAllByIsDeletedIsFalseAndMvnoId(mvnoId).stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());
        }
        /** return if no products */
        if (productIds == null || productIds.isEmpty()) {
            return getGenericDataDTO(genericDataDTO, Page.empty());
        }
        /** Paginated Fetch of ProductOwner */
        Page<ProductOwner> paginationList = productOwnerRepository.findAllByProductIdInAndOwnerIdInAndOwnerType(
                productIds, warehouseIds, CommonConstants.WAREHOUSE, pageRequest);
        /** Batch-fetch Warehouse and Product Info */
        Set<Long> ownerIds = paginationList.stream().map(ProductOwner::getOwnerId).collect(Collectors.toSet());
        Set<Long> prodIds = paginationList.stream().map(ProductOwner::getProductId).collect(Collectors.toSet());
        Map<Long, String> warehouseNameMap = warehouseManagementRepository.findAllById(ownerIds).stream()
                .collect(Collectors.toMap(WareHouse::getId, WareHouse::getName));
        Map<Long, String> productNameMap = productRepository.findAllById(prodIds).stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));
        /** Enrich records */
        paginationList.forEach(po -> {
            po.setWareHouseName(warehouseNameMap.get(po.getOwnerId()));
            po.setProductName(productNameMap.get(po.getProductId()));
        });
        return getGenericDataDTO(genericDataDTO, paginationList);
    }

    private GenericDataDTO getGenericDataDTO(GenericDataDTO genericDataDTO, Page<ProductOwner> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {
                return data;
            } catch (CustomValidationException e) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
            }
        }).collect(Collectors.toList()));
        return getGenericDataDTO(genericDataDTO, paginationList.getTotalElements(), paginationList.getNumberOfElements(), paginationList.getNumber(), paginationList.getTotalPages(), paginationList);
    }

    public static GenericDataDTO getGenericDataDTO(GenericDataDTO genericDataDTO, long totalElements, int numberOfElements, int number, int totalPages, Page<ProductOwner> paginationList) {
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        genericDataDTO.setTotalRecords(totalElements);
        genericDataDTO.setPageRecords(numberOfElements);
        genericDataDTO.setCurrentPageNumber(number + 1);
        genericDataDTO.setTotalPages(totalPages);
        return genericDataDTO;
    }
}
