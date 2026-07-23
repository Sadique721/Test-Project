package com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping;

import com.savbill.inventorymanagement.core.constants.ClientServiceConstant;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.Constants;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryDto;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.*;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserPojo;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceRepository;
import com.savbill.inventorymanagement.modules.DebitDoc.DebitDocRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.QInOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.NetworkDeviceDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.service.NetworkDeviceService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerService;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.*;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyRepository;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyService;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class InventoryMappingService extends ExBaseAbstractService<InventoryMappingDto, InventoryMapping, Long> {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    InventoryMappingRepo repository;

    @Autowired
    ProductServiceImpl productService;

    @Autowired
    InventoryMappingMapper mapper;

    @Autowired
    HierarchyRepository hierarchyRepository;

    @Autowired
    InwardMapper inwardMapper;

    //    @Autowired
//    StaffUserService staffUserService;
    @Autowired
    StaffUserRepository staffRepository;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    OutwardServiceImpl outwardService;

    @Autowired
    InwardServiceImpl inwardService;

    @Autowired
    InOutWardMACService inOutWardMACService;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    InwardRepository inwardRepository;
    @Autowired
    PopManagementService popManagementService;
    @Autowired
    ServiceAreaService serviceAreaService;
    @Autowired
    ProductCategoryService productCategoryService;

    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    @Autowired
    NetworkDeviceService networkDeviceService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    ItemServiceImpl itemService;


    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    private PopManagementRepository popManagementRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOwnerService productOwnerService;

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    InventoryMappingMapper inventoryMappingMapper;


    @Autowired
    NetworkDeviceRepository networkDeviceRepository;


    public InventoryMappingService(InventoryMappingRepo repository, InventoryMappingMapper mapper) {
        super(repository, mapper);
    }


    @Override
    public InventoryMappingRepo getRepository() {
        return repository;
    }

    @Override
    public String getModuleNameForLog() {
        return "[InventoryMappingService]";
    }

    public List<InventoryMappingDto> getInventoryMappingByStaffId(Long staffId) {
        QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId)).and(qInventoryMapping.isDeleted.eq(false)));
        return mapper.domainToDTO((List<InventoryMapping>) repository.findAll(booleanExpression), new CycleAvoidingMappingContext());
    }

    @Transactional
    @Override
    public InventoryMappingDto saveEntity(InventoryMappingDto inventoryMappingDto) throws Exception {
//        InventoryMappingDto inventoryMappingDto = null;
        try {
//            inventoryMappingDto = super.saveEntity(entity);
            if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
                inventoryMappingDto.setNextApproverId(null);
                inventoryMappingDto.setTeamHierarchyMappingId(null);
                inventoryMappingDto.setApprovalStatus(CommonConstants.PENDING);

            } else {
                if (clientServiceRepository.getByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(inventoryMappingDto.getOwnerId(), inventoryMappingDto.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(inventoryMappingDto, new CycleAvoidingMappingContext()));
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                        StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
                        StaffUser staffUser = staffRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                        inventoryMappingDto.setNextApproverId(Integer.valueOf(map.get("staffId")));
                        inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                        inventoryMappingDto.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        inventoryMappingDto.setApprovalStatus(CommonConstants.PENDING);
//                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(inventoryMappingDto.getId()), inventoryMappingDto.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                    } else {
                        inventoryMappingDto.setNextApproverId(getLoggedInUserId());
                        inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                        inventoryMappingDto.setTeamHierarchyMappingId(null);
                        inventoryMappingDto.setApprovalStatus(CommonConstants.PENDING);
                    }
                } else {
                    inventoryMappingDto.setNextApproverId(getLoggedInUserId());
                    inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                    inventoryMappingDto.setTeamHierarchyMappingId(null);
                    inventoryMappingDto.setApprovalStatus(CommonConstants.PENDING);
                }
            }
            ProductDto productDto = productService.getEntityById(inventoryMappingDto.getProductId());
            ProductCategoryDto productCategoryDto = productCategoryService.getEntityById(productDto.getProductCategory().getId());
            inventoryMappingDto.setProductId(productDto.getId());
            if (productCategoryDto.isHasMac() || productCategoryDto.isHasSerial())
                switch (productDto.getExpiryTimeUnit()) {
                    case "Day":
                        inventoryMappingDto.setExpiryDateTime(LocalDateTime.now().plusDays(productDto.getExpiryTime()));
                        break;
                    case "Month":
                        inventoryMappingDto.setExpiryDateTime(LocalDateTime.now().plusMonths(productDto.getExpiryTime()));
                        break;
                }
            //update itemHistory and Item
            if (inventoryMappingDto.getMacMappingId() != null) {
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(inventoryMappingDto.getMacMappingId().longValue()).orElse(null);
                if (inOutWardMACMapping != null) {
                    inOutWardMACMapping.setInReplacementProcess(true);
                    inOutWardMacRepo.save(inOutWardMACMapping);
                }
            }
            InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(inventoryMappingDto.getInOutWardMACMapping().get(0).getId()).orElse(null);
            if (inOutWardMACMapping.getMacAddress() == null && inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress() != null) {
                inOutWardMacRepo.save(inOutWardMACMapping);
                Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).orElse(null);
                item.setMacAddress(inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress());
                itemRepository.save(item);
                QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
                booleanExpression = booleanExpression.and(qInOutWardMACMapping.itemId.in(item.getId()).and(qInOutWardMACMapping.isForwarded.ne(-1)));
                List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
                inOutWardMACMappingList.stream().forEach(r -> {
                    r.setMacAddress(item.getMacAddress());
                    inOutWardMacRepo.save(r);
                });
            }

            //Set mac Address To InOumapping
            List<InOutWardMACMapping> inOutWardMACMappings = inventoryMappingDto.getInOutWardMACMapping();
            for (InOutWardMACMapping mapping : inOutWardMACMappings) {
                long count = Duration.between(LocalDateTime.now(), inventoryMappingDto.assignedDateTime).toDays();
                mapping.setUsedCount((int) count);
            }
//        inventoryMappingDto.setInOutWardMACMapping(inOutWardMACMappings);
//        inventoryMappingDto = super.saveEntity(inventoryMappingDto);
            if (inventoryMappingDto.inwardId != null) {
//                    InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(inventoryMappingDto.getInwardId());
                InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findById(inventoryMappingDto.getInwardId()).orElse(null), new CycleAvoidingMappingContext());
                if (inwardDto.getUnusedQty() <= 0 && inwardDto.getUsedQty() <= 0) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), " ** qty -ve **", null);
                } else {
                    inventoryMappingDto.setInOutWardMACMapping(inOutWardMACMappings);
                    inventoryMappingDto = super.saveEntity(inventoryMappingDto);
                    inwardDto.setUnusedQty(inwardDto.getUnusedQty() - inventoryMappingDto.getQty());
                    inwardDto.setUsedQty(inwardDto.getUsedQty() + inventoryMappingDto.getQty());
                    inwardService.updateEntity(inwardDto);
                }
//                InventoryMappingDto inventoryMappingDto1 = super.saveEntity(inventoryMappingDto);
                productOwnerService.updateProductOwnerForSerializedProduct(inventoryMappingDto.getQty(), inventoryMappingDto.getProductId(), Integer.valueOf(inventoryMappingDto.getStaffId()), CommonConstants.STAFF);
            }
            productOwnerService.updateProductOwnerForSerializedProduct(inventoryMappingDto.getQty(), inventoryMappingDto.getProductId(), Integer.valueOf(inventoryMappingDto.getStaffId()), CommonConstants.STAFF);
            inventoryMappingDto = super.saveEntity(inventoryMappingDto);

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return inventoryMappingDto;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        try {
            PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
            QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
            BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.isDeleted.eq(false));
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    booleanExpression = booleanExpression.and(qInventoryMapping.ownerId.eq(Long.parseLong(genericSearchModel.getFilterValue())).and(qInventoryMapping.ownerType.equalsIgnoreCase(genericSearchModel.getFilterColumn())));
                }
            }
            return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public GenericDataDTO approveInventory(Long inventoryMappingId, boolean isApproveRequest, String inventoryApprovalRemark) throws Exception {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            InventoryMappingDto entity = super.getEntityById(inventoryMappingId);
            Long oldMacMappingId = null;
            Long newInventoryMappingId = null;
            String replacementReason = null;
            if (entity.getMacMappingId() != null && entity.getReplacementReason() != null && entity.getInOutWardMACMapping() != null && entity.getInOutWardMACMapping().get(0).getInventoryMappingId() != null) {
                oldMacMappingId = entity.getMacMappingId().longValue();
                newInventoryMappingId = entity.getInOutWardMACMapping().get(0).getInventoryMappingId().longValue();
                replacementReason = entity.getReplacementReason();
            }

            entity = updateItemChanges(inventoryMappingId, isApproveRequest, inventoryApprovalRemark);

            if (entity.approvalStatus.equalsIgnoreCase("Approve")) {
                if (oldMacMappingId != null && replacementReason != null && inventoryMappingId != null) {
                    super.saveEntity(entity);
                    if (networkDeviceService.replaceNetworkDeviceBindingWithNewDevice(oldMacMappingId, newInventoryMappingId))
                        inOutWardMACService.removeInventoryfrompop(entity.getMacMappingId().longValue(), true, entity.getReplacementReason());
                }
            }

            if (oldMacMappingId != null) {
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(oldMacMappingId).orElse(null);
                if (inOutWardMACMapping != null) {
                    inOutWardMACMapping.setInReplacementProcess(false);
                    inOutWardMacRepo.save(inOutWardMACMapping);
                }
            }

            entity.setMacMappingId(null);
            entity.setReplacementReason(null);
            entity.setApprovalRemark(inventoryApprovalRemark);
            genericDataDTO.setData(super.saveEntity(entity));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public InventoryMappingDto updateItemChanges(Long inventoryMappingId, boolean isApproveRequest, String inventoryApprovalRemark) throws Exception {
        InventoryMappingDto entity;
        try {
            entity = super.getEntityById(inventoryMappingId);
            ProductDto dto = productService.getEntityById(entity.getProductId());
            ProductCategoryDto productCategoryDto = productCategoryService.getEntityById(dto.getProductCategory().getId());
            StaffUser staffUser = staffRepository.findById(Integer.valueOf(getLoggedInUserId())).get();

            if (isApproveRequest) {

                List<InOutWardMACMapping> inventoryMappings = inOutWardMacRepo.findAllByInventoryMappingId(inventoryMappingId);
                inventoryMappings.forEach(inOutWardMACMapping -> {
                    Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
                    Inward inward = inwardRepository.findById(inOutWardMACMapping.getInwardId()).get();

                    if (item.getWarranty().equalsIgnoreCase("NotStarted") || item.getWarranty().equalsIgnoreCase("Paused")) {

                        LocalDateTime localDateTime = inward.getCreatedate().plusDays(item.getWarrantyPeriod());
                        Duration setRemaingDaysDuration = Duration.between(LocalDateTime.now(), localDateTime);
                        if (setRemaingDaysDuration.toDays() == 0) {
                            try {
                                itemService.updateItemWarranty(item, "Expired");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            item.setRemainingDays(String.valueOf(0));
                        } else {
                            try {
                                itemService.updateItemWarranty(item, "InWarranty");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            item.setRemainingDays(String.valueOf(setRemaingDaysDuration.toDays()));
                            InventoryMapping mapping = inventoryMappingRepo.findById(inventoryMappingId).get();

                            if (mapping.getOwnerType().equalsIgnoreCase("Pop")) {
                                try {
                                    itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.ALLOCATED, null, null, mapping.getOwnerId(), CommonConstants.ASSIGN_INVETORIES);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (mapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                                try {
                                    itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.ALLOCATED, null, mapping.getOwnerId(), null, CommonConstants.ASSIGN_INVETORIES);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    if (entity.getOwnerType().equalsIgnoreCase("Pop")) {
                        item.setOwnerType("Pop");
                        item.setOwnerId(entity.getOwnerId());
                    }
                    if (entity.getOwnerType().equalsIgnoreCase("Service Area")) {
                        item.setOwnerType("Service Area");
                        item.setOwnerId(entity.getOwnerId());
                    }
                    itemRepository.save(item);
                });
            }

            if (!isApproveRequest) {
                if (productCategoryDto.isHasMac() || productCategoryDto.isHasSerial() || productCategoryDto.isHasTrackable()) {
                    List<InOutWardMACMapping> inventoryMappings = inOutWardMacRepo.findAllByInventoryMappingId(inventoryMappingId);
                    inventoryMappings.forEach(inOutWardMACMapping -> {
                        Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
                        InventoryMapping mapping = inventoryMappingRepo.findById(inventoryMappingId).get();
                        if (mapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            try {
                                itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.UNALLOCATED, null, null, mapping.getOwnerId(), CommonConstants.REJECT_INVETORIES);
                                item.setItemStatus("Staff Allocated");
                                itemRepository.save(item);

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (mapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            try {
                                itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.UNALLOCATED, null, mapping.getOwnerId(), null, CommonConstants.REJECT_INVETORIES);
                                item.setItemStatus("Staff Allocated");
                                itemRepository.save(item);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    for (InOutWardMACMapping inOutWardMACMapping : entity.getInOutWardMACMapping()) {
                        inOutWardMACService.removeMappingWithPopANdServiceAreaInventory(inOutWardMACMapping.getId());
                    }
                    productOwnerService.updateProductOwnerForSerializedProductReject(entity.getQty(), entity.productId, Integer.valueOf(entity.getStaffId()), CommonConstants.STAFF);
                } else {
                    productOwnerService.updateProductOwnerForNonTrackableAfterReject(entity.getQty(), entity.getProductId(), Long.valueOf(entity.getStaffId()), CommonConstants.STAFF);
                }

                entity.getInOutWardMACMapping().stream().forEach(r -> {
                    Inward inward = inwardRepository.findById(r.getInwardId()).get();
                    inward.setUnusedQty(inward.getUnusedQty() + 1);
                    inward.setUsedQty(inward.getUsedQty() - 1);
                    inwardRepository.save(inward);

                });
            }

            if (isApproveRequest) {
                entity.setApprovalStatus(CommonConstants.APPROVE);
                //Add Network Device
                if ((productCategoryDto.isHasMac() || productCategoryDto.isHasSerial()) && productCategoryDto.getType().contains("NetworkBind")) {
                    if (entity.getApprovalStatus().equalsIgnoreCase(CommonConstants.APPROVE)) {
                        /** Called: Method Create Network Device */
                        createNetworkDevice(dto, entity);
                    }
                }
            }
            if (!isApproveRequest) {
                entity.setApprovalStatus(CommonConstants.REJECTED);

            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return super.saveEntity(entity);
    }


    private void createNetworkDevice(ProductDto dto, InventoryMappingDto entity) throws Exception {
//        Entry in Network Device
        NetworkDeviceDTO networkDeviceDTO = new NetworkDeviceDTO();
        ProductCategoryDto productCategoryDto = productCategoryService.getEntityById(dto.getProductCategory().getId());
        try {
            networkDeviceDTO.setProductId(dto.getId());
            networkDeviceDTO.setStatus(dto.getStatus());
            networkDeviceDTO.setMvnoId(dto.getMvnoId());
            if (productCategoryDto.getDeviceType() != null)
                networkDeviceDTO.setDevicetype(productCategoryDto.getDeviceType());
            else
                networkDeviceDTO.setDevicetype("");
            networkDeviceDTO.setAvailableInPorts(dto.getAvailableInPorts());
            networkDeviceDTO.setTotalInPorts(dto.getTotalInPorts());
            networkDeviceDTO.setAvailableOutPorts(dto.getAvailableOutPorts());
            networkDeviceDTO.setTotalOutPorts(dto.getTotalOutPorts());
            if (dto.getAvailableOutPorts() != null && dto.getAvailableInPorts() != null) {
                networkDeviceDTO.setAvailablePorts(dto.getAvailableInPorts() + dto.getAvailableOutPorts());
                networkDeviceDTO.setTotalPorts(dto.getTotalInPorts() + dto.getTotalOutPorts());
            }

            Product product = productRepository.getOne(dto.getId());

            networkDeviceDTO.setInwardId(entity.getInwardId());
            if (entity.getOwnerType().equalsIgnoreCase("Pop")) {
                PopManagement popManagement = popManagementRepository.findById(entity.getOwnerId()).get();
                Long count = itemRepository.findAllByIsDeletedIsFalseAndOwnerIdAndOwnerType(entity.ownerId, "Pop");
                networkDeviceDTO.setName(dto.getName() + "-" + popManagement.getName() + "-" + entity.getInOutWardMACMapping().get(0).getSerialNumber() + "-" + count);
                networkDeviceDTO.setDisplayname(dto.getName() + "-" + popManagement.getName() + "-" + entity.getInOutWardMACMapping().get(0).getSerialNumber() + "-" + count);
                networkDeviceDTO.setServiceAreaIdsList(popManagementService.getEntityById(entity.getOwnerId()).getServiceAreaIdsList());
                networkDeviceDTO.setLatitude(entity.getLatitude());
                networkDeviceDTO.setLongitude(entity.getLongitude());
            } else if (entity.getOwnerType().equalsIgnoreCase("Service Area")) {
                ServiceArea serviceArea = serviceAreaRepository.findById(entity.getOwnerId()).get();
                Long count = itemRepository.findAllByIsDeletedIsFalseAndOwnerIdAndOwnerType(entity.ownerId, "Service Area");
                networkDeviceDTO.setName(dto.getName() + "-" + serviceArea.getName() + "-" + entity.getInOutWardMACMapping().get(0).getSerialNumber() + "-" + count);
                networkDeviceDTO.setDisplayname(dto.getName() + "-" + serviceArea.getName() + "-" + entity.getInOutWardMACMapping().get(0).getSerialNumber() + "-" + count);
                networkDeviceDTO.setServiceAreaNameList(Collections.singletonList((ServiceAreaDTO) serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext())));
                networkDeviceDTO.setServiceAreaIdsList(Collections.singletonList(serviceAreaService.getEntityById(entity.getOwnerId()).getId()));
                networkDeviceDTO.setLatitude(entity.getLatitude());
                networkDeviceDTO.setLongitude(entity.getLongitude());
            } else if (entity.getOwnerType().equalsIgnoreCase("Customer")) {
                Optional<Customers> customer = customersRepository.findById(entity.getOwnerId().intValue());
                Long count = itemRepository.findAllByIsDeletedIsFalseAndOwnerIdAndOwnerType(entity.ownerId, "Customer");
                if (customer.isPresent()) {
                    networkDeviceDTO.setName(dto.getName() + " - " + customer.get().getFirstname() + "-" + entity.getInOutWardMACMapping().get(0).getSerialNumber() + "-" + count);
                    networkDeviceDTO.setDisplayname(dto.getName() + " - " + customer.get().getFirstname() + "-" + entity.getInOutWardMACMapping().get(0).getSerialNumber() + "-" + count);
                }
            }
            networkDeviceDTO.setProductName(product.getName());
            networkDeviceDTO.setIsDeleted(dto.getIsDeleted());
            networkDeviceDTO.setInventorymappingId(entity.getId());
            networkDeviceDTO.setStatus("Active");
            List<InOutWardMACMapping> inventoryMappings = inOutWardMacRepo.findAllByInventoryMappingId(entity.getId());
            inventoryMappings.stream().forEach(inventoryMappings11 -> {
                Item item = itemRepository.findById(inventoryMappings11.getItemId()).get();
                networkDeviceDTO.setItemId(item.getId());
                try {
                    networkDeviceService.saveEntity(networkDeviceDTO);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }

    @Transactional
    public InOutWardMACMapping replaceInventory(Long oldMacMappingId, Long newMacMappingId) {
        try {
            InOutWardMACMapping oldInOutWardMACMapping = inOutWardMACService.getRepository().findById(oldMacMappingId).orElse(null);
            InOutWardMACMapping newInOutWardMACMapping = inOutWardMACService.getRepository().findById(newMacMappingId).orElse(null);
            if (oldInOutWardMACMapping != null) {
                InventoryMappingDto entity = super.getEntityById(oldInOutWardMACMapping.getInventoryMappingId());
                StaffUser loggedInStaffUser = staffRepository.findById(Integer.valueOf(getLoggedInUserId())).get();
                if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(entity.getOwnerId(), entity.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        oldInOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
                        oldInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        oldInOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        oldInOutWardMACMapping.setStatus("PENDING");
                        Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                        if (Long.valueOf(clientServiceRepository.findValueByNameAndMvnoId(Constants.INVENTORYCOUNTLIMIT, getMvnoIdFromCurrentStaff())) < daysDiff)
                            oldInOutWardMACMapping.setStatus("Refurbished");
                        newInOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
                        newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        newInOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        newInOutWardMACMapping.setStatus("New");
                        newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
                    } else {
                        oldInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                        oldInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        oldInOutWardMACMapping.setTeamHierarchyMappingId(null);
                        oldInOutWardMACMapping.setStatus("PENDING");
                        Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                        if (Long.valueOf(clientServiceRepository.findValueByNameAndMvnoId(Constants.INVENTORYCOUNTLIMIT, getMvnoIdFromCurrentStaff())) < daysDiff)
                            oldInOutWardMACMapping.setStatus("Refurbished");
                        oldInOutWardMACMapping.setUsedCount(Math.toIntExact(daysDiff));
                        newInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                        newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        newInOutWardMACMapping.setTeamHierarchyMappingId(null);
                        newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
                        newInOutWardMACMapping.setStatus("New");
                    }
                } else {
                    oldInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                    oldInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                    oldInOutWardMACMapping.setTeamHierarchyMappingId(null);
                    oldInOutWardMACMapping.setStatus("PENDING");
                    Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                    if (Long.valueOf(clientServiceRepository.findValueByNameAndMvnoId(Constants.INVENTORYCOUNTLIMIT, getMvnoIdFromCurrentStaff())) < daysDiff)
                        oldInOutWardMACMapping.setStatus("Refurbished");
                    oldInOutWardMACMapping.setUsedCount(Math.toIntExact(daysDiff));
                    newInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                    newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                    newInOutWardMACMapping.setTeamHierarchyMappingId(null);
                    newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
                    newInOutWardMACMapping.setStatus("New");
                }
                inOutWardMACService.getRepository().save(oldInOutWardMACMapping);
                return inOutWardMACService.getRepository().save(newInOutWardMACMapping);
            } else {
                throw new RuntimeException("No mapping found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(e.getMessage());

        }
        return null;
    }

    @Transactional
    public GenericDataDTO approveReplaceInventory(Long macMappingId, boolean billAble, boolean isApproveRequest) throws Exception {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            InOutWardMACMapping inOutWardMACMapping = inOutWardMACService.getRepository().findById(macMappingId).orElse(null);
            InventoryMappingDto entity = super.getEntityById(inOutWardMACMapping.getCustInventoryMappingId());
            ProductDto dto = productService.getEntityById(entity.getProductId());
            if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
                entity.setNextApproverId(null);
                entity.setTeamHierarchyMappingId(null);
                entity.setApprovalStatus(CommonConstants.APPROVE);
                genericDataDTO.setData(super.saveEntity(entity));
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }
//        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId());
            StaffUser loggedInUser = staffRepository.findById(Integer.valueOf(getLoggedInUserId())).get();
            if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(entity.getOwnerId(), entity.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, true, false, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                    inOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
                    inOutWardMACMapping.setPreviousApproveId(loggedInUser.getId());
                    inOutWardMACMapping.setStatus("PENDING");
                    inOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                } else {
                    inOutWardMACMapping.setCurrentApproveId(null);
                    inOutWardMACMapping.setPreviousApproveId(loggedInUser.getId());
                    inOutWardMACMapping.setStatus("ACTIVE");
                    inOutWardMACMapping.setTeamHierarchyMappingId(null);
                    /*if (!billAble) {
                        inOutWardMACMapping.setCustInventoryMappingId(null);
                        endMacMapppingService.deleteByMacAddress(inOutWardMACMapping.getMacAddress(), customers.getId());
                    } else {
                        EndMacMappping custMacMappping = new EndMacMappping();
                        custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                        endMacMapppingService.save(custMacMappping);
                    }*/
                }
            } else {
                Map<String, Object> map = hierarchyService.getTeamForNextApprove(getMvnoIdForWorkflow(entity.getOwnerId(), entity.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, true, false, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
                if (map.containsKey("assignableStaff")) {
                    genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    return genericDataDTO;
                } else {
                    inOutWardMACMapping.setCurrentApproveId(null);
                    inOutWardMACMapping.setPreviousApproveId(loggedInUser.getId());
                    inOutWardMACMapping.setStatus("ACTIVE");
                    inOutWardMACMapping.setTeamHierarchyMappingId(null);
                    /*if (!billAble) {
                        inOutWardMACMapping.setCustInventoryMappingId(null);
                        endMacMapppingService.deleteByMacAddress(inOutWardMACMapping.getMacAddress(), customers.getId());
                    } else {
                        EndMacMappping custMacMappping = new EndMacMappping();
                        custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                        endMacMapppingService.save(custMacMappping);
                    }*/
                }
            }

            genericDataDTO.setData(inOutWardMACService.getRepository().save(inOutWardMACMapping));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getStaffDetails(Long inventoryMappingId) {
//        StaffUser staffUser = staffUserService.get(getLoggedInUserId());
        StaffUser staffUser = staffRepository.findById(Integer.valueOf(getLoggedInUserId())).get();
        return staffUser.getUsername();
    }

    private Integer getMvnoIdForWorkflow(Long ownerId, String ownerType) {
        Integer mvnoId = 0;
        try {
            PopManagementDTO popManagement = null;
            ServiceAreaDTO serviceArea = null;
            if (ownerType.equalsIgnoreCase(CommonConstants.POP)) {
                popManagement = popManagementService.getEntityById(ownerId);
                mvnoId = popManagement.getMvnoId();
            } else if (ownerType.equalsIgnoreCase(CommonConstants.SERVICE_AREA)) {
                serviceArea = serviceAreaService.getEntityById(ownerId);
                mvnoId = serviceArea.getMvnoId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
        return mvnoId;
    }

    @Transactional
    public InventoryMappingDto saveNonSerializedEntity(InventoryMappingDto entity) throws Exception {
        try {
            if (entity.getQty() == null) {
                throw new Exception("Please Enter Assign Quantity");
            } else {
                InventoryMappingDto inventoryMappingDto = entity;
                ProductDto productDto = productService.getEntityById(inventoryMappingDto.getProductId());
                Product product = productRepository.findById(productDto.getId()).get();
                boolean hasSerial = product.getProductCategory().isHasSerial();
                boolean isTrackable = product.getProductCategory().isHasTrackable();
                if (!hasSerial && !isTrackable) {
                    if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
                        inventoryMappingDto.setNextApproverId(null);
                        inventoryMappingDto.setTeamHierarchyMappingId(null);
                        inventoryMappingDto.setApprovalStatus(CommonConstants.APPROVE);

                    } else {
                        if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(inventoryMappingDto.getOwnerId(), inventoryMappingDto.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(inventoryMappingDto, new CycleAvoidingMappingContext()));
                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                                StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
                                StaffUser staffUser = staffRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                                inventoryMappingDto.setNextApproverId(Integer.valueOf(map.get("staffId")));
                                inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                                inventoryMappingDto.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                inventoryMappingDto.setApprovalStatus(CommonConstants.PENDING);
//                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(inventoryMappingDto.getId()), inventoryMappingDto.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                            } else {
                                inventoryMappingDto.setNextApproverId(getLoggedInUserId());
                                inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                                inventoryMappingDto.setTeamHierarchyMappingId(null);
                                inventoryMappingDto.setApprovalStatus(CommonConstants.PENDING);
                            }
                        } else {
                            inventoryMappingDto.setNextApproverId(getLoggedInUserId());
                            inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                            inventoryMappingDto.setTeamHierarchyMappingId(null);
                            inventoryMappingDto.setApprovalStatus(CommonConstants.PENDING);
                        }
                    }
                    saveEntityForNonTrackable(inventoryMappingDto);
                    productOwnerService.updateProductOwnerForNonTrackable(entity.getQty(), entity.productId, Long.valueOf(entity.staffId), CommonConstants.STAFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return entity;
    }

    public InventoryMappingDto saveEntityForNonTrackable(InventoryMappingDto entity) throws Exception {
        InventoryMappingDto inventoryMappingDto = null;
        try {
            //entity.setItemId(entity.getProductId());
            inventoryMappingDto = super.saveEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return inventoryMappingDto;
    }

    public void validateMac(InventoryMappingDto inventoryMappingDto) throws Exception {
        if (inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter mac in selected item", null);
        } else {
            validateMacInItem(inventoryMappingDto);
        }
    }

    public void validateMacInItem(InventoryMappingDto inventoryMappingDto) throws Exception {

//        ItemDto itemDto = itemService.getEntityForUpdateAndDelete(inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId());
        Item item = itemRepository.findById(inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId()).orElse(null);
        if (item != null && item.getMacAddress() == null && inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress() != null) {
            item.setMacAddress(inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress());
            itemRepository.save(item);
        }
        if ( item != null && item.getMacAddress() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update mac in selected item", null);
        }
    }

    public void validateSerialNumber(InventoryMappingDto inventoryMappingDto) throws Exception {
        if (inventoryMappingDto.getInOutWardMACMapping().get(0).getSerialNumber() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter serial number in selected item", null);
        } else {
            validateSerialNumberInItem(inventoryMappingDto);
        }
    }

    public void validateSerialNumberInItem(InventoryMappingDto inventoryMappingDto) throws Exception {
//        ItemDto itemDto = itemService.getEntityForUpdateAndDelete(inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId());
        String serialnumber =  itemRepository.findSerialNumberByItemId(inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId());
        if (serialnumber == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update serial number in selected item", null);
        }
    }

    public GenericDataDTO getPopInventoryMappingByStaffId(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId, boolean isGetSerializedItem) {
        String SUBMODULE = getModuleNameForLog() + " [getPopInventoryMappingByStaffId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
            PageRequest pageRequest;
            Page<InventoryMapping> inventoryMappingPage = null;
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (isGetSerializedItem) {
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop"));
                if (getMvnoIdFromCurrentStaff() != 1) {
                    booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                }
                inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        PopManagement popManagement = popManagementRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setPopName(popManagement.getName());
                    });
                }
                List<InventoryMappingDto> mappingDtos = inventoryMappingMapper.domainToDTO(inventoryMappingPage.getContent(), new CycleAvoidingMappingContext());
                mappingDtos.stream().forEach(dto -> {
                    if (dto.getProductId() != null) {
                        Product product = productRepository.findById(dto.productId).orElse(null);
                        if (product != null)
                            dto.setDeviceType(product.getProductCategory().getDeviceType());

                        if (dto.getInOutWardMACMapping() != null && !dto.getInOutWardMACMapping().isEmpty() && dto.getInOutWardMACMapping().get(0).getInventoryMappingId() != null) {
                            NetworkDevices devices = networkDeviceRepository.findByInventorymappingId(dto.getInOutWardMACMapping().get(0).getInventoryMappingId());
                            if (devices != null) {
                                dto.setDeviceName(devices.getName());
                                dto.setTotalInPort(devices.getTotalInPorts());
                                dto.setAvailableInPort(devices.getAvailableInPorts());
                                dto.setTotalOutPort(devices.getTotalOutPorts());
                                dto.setAvailableOutPort(devices.getAvailableOutPorts());
                                dto.setTotalPort(devices.getTotalPorts());
                                dto.setAvailablePort(devices.getAvailablePorts());
                            }
                        }
                    }
                });
                genericDataDTO.setDataList(mappingDtos);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
            }
            if (!isGetSerializedItem) {
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop"));
                if (getMvnoIdFromCurrentStaff() != 1) {
                    booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                }
                inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        PopManagement popManagement = popManagementRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setPopName(popManagement.getName());
                    });
                }

                List<InventoryMappingDto> mappingDtos = inventoryMappingMapper.domainToDTO(inventoryMappingPage.getContent(), new CycleAvoidingMappingContext());
                mappingDtos.stream().forEach(dto -> {
                    if (dto.getProductId() != null) {
                        Product product = productRepository.findById(dto.productId).orElse(null);
                        if (product != null)
                            dto.setDeviceType(product.getProductCategory().getDeviceType());

                        if (dto.getInOutWardMACMapping() != null && !dto.getInOutWardMACMapping().isEmpty() && dto.getInOutWardMACMapping().get(0).getInventoryMappingId() != null) {
                            NetworkDevices devices = networkDeviceRepository.findByInventorymappingId(dto.getInOutWardMACMapping().get(0).getInventoryMappingId());
                            if (devices != null) {
                                dto.setDeviceName(devices.getName());
                                dto.setTotalInPort(devices.getTotalInPorts());
                                dto.setAvailableInPort(devices.getAvailableInPorts());
                                dto.setTotalOutPort(devices.getTotalOutPorts());
                                dto.setAvailableOutPort(devices.getAvailableOutPorts());
                                dto.setTotalPort(devices.getTotalPorts());
                                dto.setAvailablePort(devices.getAvailablePorts());
                            }
                        }
                    }
                });
                genericDataDTO.setDataList(mappingDtos);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public GenericDataDTO getServiceAreaInventoryMappingByStaffId(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId, boolean isGetSerializedItem) {
        String SUBMODULE = getModuleNameForLog() + " [getServiceAreaInventoryMappingByStaffId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
            PageRequest pageRequest;
            Page<InventoryMapping> inventoryMappingPage = null;
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (isGetSerializedItem) {
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("Service Area"));
                if (getMvnoIdFromCurrentStaff() != 1) {
                    booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                }
                inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        ServiceArea serviceArea = serviceAreaRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setServiceAreaName(serviceArea.getName());
                    });
                }

                List<InventoryMappingDto> mappingDtos = inventoryMappingMapper.domainToDTO(inventoryMappingPage.getContent(), new CycleAvoidingMappingContext());
                mappingDtos.stream().forEach(dto -> {
                    if (dto.getProductId() != null) {
                        Product product = productRepository.findById(dto.productId).orElse(null);
                        if (product != null)
                            dto.setDeviceType(product.getProductCategory().getDeviceType());

                        if (dto.getInOutWardMACMapping() != null && !dto.getInOutWardMACMapping().isEmpty() && dto.getInOutWardMACMapping().get(0).getInventoryMappingId() != null) {
                            NetworkDevices devices = networkDeviceRepository.findByInventorymappingId(dto.getInOutWardMACMapping().get(0).getInventoryMappingId());
                            if (devices != null) {
                                dto.setDeviceName(devices.getName());
                                dto.setTotalInPort(devices.getTotalInPorts());
                                dto.setAvailableInPort(devices.getAvailableInPorts());
                                dto.setTotalOutPort(devices.getTotalOutPorts());
                                dto.setAvailableOutPort(devices.getAvailableOutPorts());
                                dto.setTotalPort(devices.getTotalPorts());
                                dto.setAvailablePort(devices.getAvailablePorts());
                            }
                        }
                    }
                });
                genericDataDTO.setDataList(mappingDtos);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
            }
            if (!isGetSerializedItem) {
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.containsIgnoreCase("Service Area"));
                if (getMvnoIdFromCurrentStaff() != 1) {
                    booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                }
                inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        ServiceArea serviceArea = serviceAreaRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setServiceAreaName(serviceArea.getName());
                    });
                }

                List<InventoryMappingDto> mappingDtos = inventoryMappingMapper.domainToDTO(inventoryMappingPage.getContent(), new CycleAvoidingMappingContext());
                mappingDtos.stream().forEach(dto -> {
                    if (dto.getProductId() != null) {
                        Product product = productRepository.findById(dto.productId).orElse(null);
                        if (product != null)
                            dto.setDeviceType(product.getProductCategory().getDeviceType());

                        if (dto.getInOutWardMACMapping() != null && !dto.getInOutWardMACMapping().isEmpty() && dto.getInOutWardMACMapping().get(0).getInventoryMappingId() != null) {
                            NetworkDevices devices = networkDeviceRepository.findByInventorymappingId(dto.getInOutWardMACMapping().get(0).getInventoryMappingId());
                            if (devices != null) {
                                dto.setDeviceName(devices.getName());
                                dto.setTotalInPort(devices.getTotalInPorts());
                                dto.setAvailableInPort(devices.getAvailableInPorts());
                                dto.setTotalOutPort(devices.getTotalOutPorts());
                                dto.setAvailableOutPort(devices.getAvailableOutPorts());
                                dto.setTotalPort(devices.getTotalPorts());
                                dto.setAvailablePort(devices.getAvailablePorts());
                            }
                        }
                    }
                });
                genericDataDTO.setDataList(mappingDtos);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }
}
