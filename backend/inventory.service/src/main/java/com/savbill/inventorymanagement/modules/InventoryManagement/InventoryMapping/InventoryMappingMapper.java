package com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping;

import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamHierarchyMapping.TeamHierarchyMapping;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamHierarchyMapping.TeamHierarchyMappingRepo;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class InventoryMappingMapper implements IBaseMapper<InventoryMappingDto, InventoryMapping> {

    @Mappings({
        @Mapping(source = "staff.id", target = "staffId"),
//    @Mapping(source = "customer.id", target = "customerId"),
        @Mapping(source = "product.id", target = "productId"),
        @Mapping(source = "nextApprover.id", target = "nextApproverId"),
        @Mapping(source = "teamHierarchyMapping.id", target = "teamHierarchyMappingId")
    })
    @Override
    public abstract InventoryMappingDto domainToDTO(InventoryMapping customerInventoryMapping, @Context CycleAvoidingMappingContext context);

    @Mappings({
        @Mapping(source = "staffId", target = "staff"),
            //    @Mapping(source = "customerId", target = "customer"),
        @Mapping(source = "productId", target = "product"),
        @Mapping(source = "nextApproverId", target = "nextApprover"),
        @Mapping(source = "teamHierarchyMappingId", target = "teamHierarchyMapping")
    })
    @Override
    public abstract InventoryMapping dtoToDomain(InventoryMappingDto dtoData, @Context CycleAvoidingMappingContext context);

//    @Autowired
//    CustomersService customersService;
    @Autowired
    OutwardServiceImpl outwardService;

    @Autowired
    InwardServiceImpl inwardService;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    StaffUserRepository staffRepository;

//    @Autowired
//    StaffUserService staffUserService;

    @Autowired
    ProductServiceImpl productService;

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @AfterMapping
    void afterMapping(@MappingTarget InventoryMappingDto inventoryMappingDto, InventoryMapping customerInventoryMapping) {
        if (customerInventoryMapping.getInward() != null) {
            inventoryMappingDto.setInwardNumber(customerInventoryMapping.getInward().getInwardNumber());
        } else {
            inventoryMappingDto.setInwardNumber("-");
        }

        if (customerInventoryMapping.getProduct() != null) {
            inventoryMappingDto.setProductName(customerInventoryMapping.getProduct().getName());
            inventoryMappingDto.setHasMac(customerInventoryMapping.getProduct().getProductCategory().isHasMac());
            inventoryMappingDto.setHasSerial(customerInventoryMapping.getProduct().getProductCategory().isHasSerial());
            inventoryMappingDto.setHasTrackable(customerInventoryMapping.getProduct().getProductCategory().isHasTrackable());
            inventoryMappingDto.setHasPort(customerInventoryMapping.getProduct().getProductCategory().isHasPort());
        } else {
            inventoryMappingDto.setProductName("-");
            inventoryMappingDto.setHasMac(false);
            inventoryMappingDto.setHasSerial(false);
            inventoryMappingDto.setHasTrackable(false);
            inventoryMappingDto.setHasPort(false);
        }

        if (customerInventoryMapping.getNextApprover() != null) {
            inventoryMappingDto.setAssigneeName(customerInventoryMapping.getNextApprover().getFullName());
        } else {
            inventoryMappingDto.setAssigneeName("-");
        }

    }

    Integer fromCustomerToCustomerId(Customers customer) {
        return null != customer ? customer.getId() : null;
    }

    Customers fromCustomerIdToCustomer(Integer customerId) {
//        return null != customersService.get(customerId) ? customersService.get(customerId) : null;
        return null != customersRepository.findById(customerId) ? customersRepository.findById(customerId).get() : null;
    }

    Integer fromStaffToStaffId(StaffUser staffUser) {
        return null != staffUser ? staffUser.getId() : null;
    }

    StaffUser fromStaffIdToStaff(Integer staffId) {
        if (staffId != null) {
//            return staffUserService.getRepository().findById(staffId).isPresent() ? staffUserService.getRepository().findById(staffId).get() : null;
            return staffRepository.findById(Integer.valueOf(staffId)).isPresent() ? staffRepository.findById(Integer.valueOf(staffId)).get() : null;
        } else {
            return null;
        }

    }

    Long fromProductToProductId(Product product) {
        return null != product ? product.getId() : null;
    }

    Product fromProductIdToProduct(Long productId) {
        return productService.getRepository().findById(productId).isPresent() ? productService.getRepository().findById(productId).get() : null;
    }

    //        StaffUser fromNextApproverIdToNextApprover(Integer nextApproverId) {
//        return  staffUserService.getRepository().findById(nextApproverId).isPresent() ?  staffUserService.getRepository().findById(nextApproverId).get() : null;
//    }
    TeamHierarchyMapping fromTeamHierarchyMappingIdToTeamHierarchyMapping(Integer teamHierarchyMappingId) {
        if (teamHierarchyMappingId != null) {
            return teamHierarchyMappingRepo.findById(teamHierarchyMappingId).isPresent() ? teamHierarchyMappingRepo.findById(teamHierarchyMappingId).get() : null;
        } else {
            return null;
        }
    }

    Integer fromTeamHierarchyMappingToTeamHierarchyMappingId(TeamHierarchyMapping teamHierarchyMapping) {
        return null != teamHierarchyMapping ? teamHierarchyMapping.getId() : null;
    }
}
