package com.savbill.inventorymanagement.utils;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserPojo;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserMapper;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.ChargeManagement.Charge;
import com.savbill.inventorymanagement.modules.ChargeManagement.ChargeRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMappping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMapppingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.QCustMacMappping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.QCustomerInventoryMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.BusinessUnit.BusinessUnit;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.PlanService.PlanService;
import com.savbill.inventorymanagement.modules.PlanService.PlanServiceRepository;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.Tax;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.TaxRepository;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class
WorkFlowQueryUtils {

    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    TaxRepository taxRepository;
    @Autowired
    PlanServiceRepository planServiceRepository;
    @Autowired
    CustMacMappingService custMacMapppingService;
    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;
    @Autowired
    StaffUserMapper staffUserMapper;

    public void checkAction(String actionName, String eventName, Object entity) {
        switch (eventName) {
            case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                if (entity instanceof CustomerInventoryMapping) {
                    CustomerInventoryMapping customerInventoryMapping = (CustomerInventoryMapping) entity;
                    //inventory assign
                    switch (actionName) {
                        case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_ACTION.INVOICE_GENERATION: {
                            generateInvoiceForInventory(customerInventoryMapping);
                            break;
                        }
                        case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_ACTION.MAC_CHANGE_PROVISION: {
                            addMACAddressInventory(customerInventoryMapping);
                            break;
                        }
                    }
                }
                break;
            }
            default: {
                System.out.println("please enter valid input");
            }
        }
    }
    private void generateInvoiceForInventory(CustomerInventoryMapping customerInventoryMapping) {
        if (customerInventoryMapping.getProduct().getRefurburshiedProductCharge() != null) {
            // Charge charge = chargeService.get(customerInventoryMapping.getProduct().getRefurburshiedProductCharge());
            Charge charge = chargeRepository.findById(customerInventoryMapping.getProduct().getRefurburshiedProductCharge().getId()).get();
            Tax taxEntity = taxRepository.findById(charge.getTaxId()).get();
            Double applicableAmount = charge.getPrice() + ((charge.getPrice() * taxEntity.getTieredList().get(0).getRate()) / 100.0);
//            Runnable chargeRunnable = new ChargeThread(customerInventoryMapping.getCustomer().getId(), new ArrayList<>(), customersService, customerInventoryMapping.getId(), "", null);
//            Thread billChargeThread = new Thread(chargeRunnable);
//            billChargeThread.start();
        }

    }
    private void addMACAddressInventory(CustomerInventoryMapping customerInventoryMapping) {
        customerInventoryMapping.getInOutWardMACMapping().forEach(inOutWardMACMapping -> {
                    PlanService planService = planServiceRepository.findById(Math.toIntExact(customerInventoryMapping.getServiceId())).orElse(null);
                    if (planService.getIs_dtv()==false) {
                        QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
                        List<CustMacMappping> customerMacMapping = (List<CustMacMappping>) custMacMapppingRepository.findAll(qCustMacMappping.macAddress.eq(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.customer.id.eq(customerInventoryMapping.getCustomer().getId())));
                        if (customerMacMapping.size() == 0) {
                            CustMacMappping custMacMappping = new CustMacMappping();
                            custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                            custMacMappping.setCustomer(customerInventoryMapping.getCustomer());
                            custMacMapppingService.save(custMacMappping);
                        }
                    }
                }

        );
    }
    public List<StaffUserPojo> assignCAFToStaffFromTeam(List<Long> serviceAreaList, Long buId, Teams team) {
        List<StaffUser> tempStaffList = new ArrayList<>();
        Set<StaffUserPojo> returnList;
        Set<StaffUser> staffList = team.getStaffUser();
        staffList = staffList.stream().filter(staffUser -> staffUser.getStatus().equals(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toSet());
        if (staffList != null && staffList.size() > 0) {
            for (StaffUser staff : staffList) {
                if (!staff.getIsDelete() && staff.getStatus().equalsIgnoreCase("Active")) {
                    if (staff.getServiceAreaNameList() != null && staff.getServiceAreaNameList().size() > 0) {
                        for (ServiceArea serviceArea : staff.getServiceAreaNameList()) {
                            if (serviceAreaList.stream().anyMatch(serviceArea1 -> serviceArea1.equals(serviceArea.getId()))) {
                                if (staff.getBusinessUnitNameList().size() > 0) {
                                    if (buId != null && buId != 0) {
                                        for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
                                            if (buId.equals(businessUnit.getId())) {
                                                if (tempStaffList.stream().noneMatch(staffUser -> Objects.equals(staffUser.getId(), staff.getId()))) {
                                                    tempStaffList.add(staff);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    tempStaffList.add(staff);
                                }
                            }
                        }
                    } else if (staff.getServiceAreaNameList().size() == 0) {
                        if (staff.getBusinessUnitNameList().size() > 0) {
                            if (buId != null && buId != 0) {
                                for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
                                    if (buId == businessUnit.getId().longValue()) {
                                        if (tempStaffList.stream().noneMatch(staffUser -> Objects.equals(staffUser.getId(), staff.getId()))) {
                                            tempStaffList.add(staff);
                                        }
                                    }
                                }
                            }
                        } else {
                            tempStaffList.add(staff);
                        }

                    }
                }
            }
        }
        returnList = tempStaffList.stream().map(staffUser -> staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext())).collect(Collectors.toSet());
        List<StaffUserPojo> staffUserList = returnList.stream().collect(Collectors.toList());
        return staffUserList;
    }

    public int assignStaffFromList(List<StaffUserPojo> staffList, String eventName, Object entity) {
        int staffId = 0;
        Long count;
        if (staffList.size() > 0) {
            HashMap<Integer, Long> countListMap = new HashMap<>();
            for (StaffUserPojo staffUserTemp : staffList) {
                if (entity instanceof CustomerInventoryMapping && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN)) {
                    QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                    count = customerInventoryMappingRepo.count(qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.nextApprover.id.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
                }
            }
            if (countListMap.values().size() == 0) {
                return staffId;
            } else {
                Long minValueInMap = Collections.min(countListMap.values());
                // This will return min value in the HashMap
                for (Map.Entry<Integer, Long> entry : countListMap.entrySet()) {  // Iterate through HashMap
                    if (Objects.equals(entry.getValue(), minValueInMap)) {
                        staffId = entry.getKey();     // staff id with minimum reuqest
                    }
                }
                if (countListMap.size() > 0 && staffId != 0) {
                    return staffId;
                }
            }
        }

        return staffId;
    }

}
