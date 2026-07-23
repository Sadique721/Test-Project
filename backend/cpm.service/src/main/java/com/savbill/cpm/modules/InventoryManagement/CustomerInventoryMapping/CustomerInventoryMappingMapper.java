package com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.common.StaffUser;
import com.savbill.cpm.modules.InventoryManagement.ItemGroup.ItemAssemblyMapper;
import com.savbill.cpm.modules.InventoryManagement.ItemGroup.ItemAssemblyServiceImp;
import com.savbill.cpm.modules.InventoryManagement.outward.OutwardServiceImpl;
import com.savbill.cpm.modules.InventoryManagement.product.Product;
import com.savbill.cpm.modules.InventoryManagement.product.ProductServiceImpl;
import com.savbill.cpm.modules.Teams.domain.TeamHierarchyMapping;
import com.savbill.cpm.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.service.common.StaffUserService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class CustomerInventoryMappingMapper implements IBaseMapper<CustomerInventoryMappingDto, CustomerInventoryMapping> {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "staff.id", target = "staffId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "nextApprover.id", target = "nextApproverId")
    @Mapping(source = "teamHierarchyMappingId", target = "teamHierarchyMappingId")
    @Override
    public abstract CustomerInventoryMappingDto domainToDTO(CustomerInventoryMapping customerInventoryMapping, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "customerId", target = "customer")
    @Mapping(source = "staffId", target = "staff")
    @Mapping(source = "productId", target = "product")
    @Mapping(source = "nextApproverId", target = "nextApprover")
    @Mapping(source = "teamHierarchyMappingId", target = "teamHierarchyMappingId")
    @Override
    public abstract CustomerInventoryMapping dtoToDomain(CustomerInventoryMappingDto dtoData, @Context CycleAvoidingMappingContext context);

    @Autowired
    CustomersService customersService;
    @Autowired
    OutwardServiceImpl outwardService;


    @Autowired
    StaffUserService staffUserService;

    @Autowired
    ProductServiceImpl productService;

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    ItemAssemblyServiceImp itemAssemblyServiceImp;

    @Autowired
    ItemAssemblyMapper itemAssemblyMapper;

    @AfterMapping
    void afterMapping(@MappingTarget CustomerInventoryMappingDto customerInventoryMappingDto, CustomerInventoryMapping customerInventoryMapping) {

        if (customerInventoryMapping.getProduct() != null) {
            customerInventoryMappingDto.setProductName(customerInventoryMapping.getProduct().getName());
            customerInventoryMappingDto.setHasMac(customerInventoryMapping.getProduct().getProductCategory().isHasMac());
            customerInventoryMappingDto.setHasSerial(customerInventoryMapping.getProduct().getProductCategory().isHasSerial());
            customerInventoryMappingDto.setHasTrackable(customerInventoryMapping.getProduct().getProductCategory().isHasTrackable());
            customerInventoryMappingDto.setHasPort(customerInventoryMapping.getProduct().getProductCategory().isHasPort());
            customerInventoryMappingDto.setHasCas(customerInventoryMapping.getProduct().getProductCategory().isHasCas());
        } else {
            customerInventoryMappingDto.setProductName("-");
            customerInventoryMappingDto.setHasMac(false);
            customerInventoryMappingDto.setHasSerial(false);
            customerInventoryMappingDto.setHasTrackable(false);
            customerInventoryMappingDto.setHasPort(false);
            customerInventoryMappingDto.setHasCas(false);
        }

        if (customerInventoryMapping.getCustomer() != null) {
            customerInventoryMappingDto.setCustomerName(customerInventoryMapping.getCustomer().getCustname());
        } else {
            customerInventoryMappingDto.setCustomerName("-");

        }
        if (customerInventoryMapping.getNextApprover() != null) {
            customerInventoryMappingDto.setAssigneeName(customerInventoryMapping.getNextApprover().getUsername());
        } else {
            customerInventoryMappingDto.setAssigneeName("-");
        }

    }

    Integer fromCustomerToCustomerId(Customers customer) {
        return null != customer ? customer.getId() : null;
    }

    Customers fromCustomerIdToCustomer(Integer customerId) {
        return null != customersService.get(customerId) ? customersService.get(customerId) : null;
    }


    Integer fromStaffToStaffId(StaffUser staffUser) {
        return null != staffUser ? staffUser.getId() : null;
    }

    StaffUser fromStaffIdToStaff(Integer staffId) {
        if (staffId != null) {
            return staffUserService.getRepository().findById(staffId).isPresent() ? staffUserService.getRepository().findById(staffId).get() : null;
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
