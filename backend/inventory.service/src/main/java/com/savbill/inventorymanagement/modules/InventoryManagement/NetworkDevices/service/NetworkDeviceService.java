package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.service;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.Customers.*;
import com.savbill.inventorymanagement.modules.Customers.CustomerNetworkBind;
import com.savbill.inventorymanagement.modules.Customers.CustomerNetworkBindRepository;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.Inward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.QInward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper.NetworkDeviceBindMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper.NetworkDeviceMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper.SloatMapper.NetworkConvertor;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceBindingsRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkdeviceBindRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.*;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.*;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.savbill.inventorymanagement.utils.APIConstants;
import com.savbill.inventorymanagement.utils.TypeConstants;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The type Network device service.
 */
@Service
public class NetworkDeviceService extends ExBaseAbstractService<NetworkDeviceDTO, NetworkDevices, Long> {

    /**
     * The constant MODULE.
     */
    public static final String MODULE = " [NetworkDeviceService] ";

    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(NetworkDeviceService.class);

    /**
     * The Network device repository.
     */
    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;

    /**
     * The Inventory mapping repo.
     */
    @Autowired
    InventoryMappingRepo inventoryMappingRepo;

    /**
     * The Item repository.
     */
    @Autowired
    ItemRepository itemRepository;
    /**
     * The Network device mapper.
     */
    @Autowired
    private NetworkDeviceMapper networkDeviceMapper;
    /**
     * The Service area mapper.
     */
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    /**
     * The Product repository.
     */
    @Autowired
    private ProductRepository productRepository;

    /**
     * The Service area repository.
     */
    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    /**
     * The Networkdevice bind repository.
     */
    @Autowired
    private NetworkdeviceBindRepository networkdeviceBindRepository;

    /**
     * The Network convertor.
     */
    @Autowired
    NetworkConvertor networkConvertor;

    /**
     * The Network device bind mapper.
     */
    @Autowired
    NetworkDeviceBindMapper networkDeviceBindMapper;

    /**
     * The In out ward mac repo.
     */
    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    /**
     * The Entity manager.
     */
    @PersistenceContext
    EntityManager entityManager;

    /**
     * The Network device bindings repository.
     */
    @Autowired
    NetworkDeviceBindingsRepository networkDeviceBindingsRepository;
    /**
     * The Customers repository.
     */
    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    CustomerNetworkBindRepository customerNetworkBindRepository;

    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    PopManagementRepository popManagementRepository;


    /**
     * Instantiates a new Network device service.
     *
     * @param repository the repository
     * @param mapper the mapper
     */
    public NetworkDeviceService(NetworkDeviceRepository repository, NetworkDeviceMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "id");
        sortColMap.put("type", "devicetype");
        sortColMap.put("areaName", "name");
    }

    /**
     * Gets module name for log.
     *
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[NetworkDeviceService]";
    }


    /**
     * Update network device.
     *
     * @param dto the dto
     * @param oltSlots the olt slots
     */
    public void UpdateNetworkDevice(NetworkDeviceDTO dto, List<Oltslots> oltSlots) {
        String SUBMODULE = MODULE + "[UpdateNetworkDevice()]";
        NetworkDevices networkDevices = new NetworkDevices();
        try {
            networkDevices.setId(dto.getId());
            networkDevices.setName(dto.getName());
            networkDevices.setDisplayname(dto.getDisplayname());
            networkDevices.setDevicetype(dto.getDevicetype());
            List<ServiceArea> serviceArea = serviceAreaMapper.dtoToDomain(dto.getServiceAreaNameList(), new CycleAvoidingMappingContext());
            networkDevices.setServiceAreaNameList(serviceArea);
            networkDevices.setOltslotsList(oltSlots);
            networkDevices.setStatus(dto.getStatus());
            networkDeviceRepository.save(networkDevices);

        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Gets network devices by service area id.
     *
     * @param serviceAreaId the service area id
     * @return the network devices by service area id
     */
    public List<NetworkDeviceDTO> getNetworkDevicesByServiceAreaId(Long serviceAreaId) {
        List<NetworkDevices> networkDevicesList = networkDeviceRepository.findByServiceareaIdAndIsDeletedIsFalse(serviceAreaId);
        List<NetworkDeviceDTO> networkDeviceDTOList = networkDevicesList.stream().filter(data -> data.getDevicetype().equalsIgnoreCase(TypeConstants.OLT) && data.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || data.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).map(data -> networkDeviceMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return networkDeviceDTOList;
    }

    /**
     * Find by name and device type network devices.
     *
     * @param serviceName the service name
     * @param deviceType the device type
     * @return the network devices
     */
    public NetworkDevices findByNameAndDeviceType(String serviceName, String deviceType) {
        NetworkDevices networkDevices = new NetworkDevices();
        List<NetworkDevices> networkDevicesList = networkDeviceRepository.findByNameAndDevicetypeAndIsDeletedIsFalse(serviceName, deviceType)
                .stream().filter(data -> data.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || data.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());

        if (null != networkDevicesList && 0 < networkDevicesList.size()) {
            networkDevices = networkDevicesList.get(0);
        }
        return networkDevices;
    }

//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("NetworkDevice");
//        createExcel(workbook, sheet, NetworkDeviceDTO.class, null);
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        createPDF(doc, NetworkDeviceDTO.class, null);
//    }

    /**
     * Gets device by name or type or area name.
     *
     * @param s1 the s 1
     * @param pageRequest the page request
     * @return the device by name or type or area name
     */
    public GenericDataDTO getDeviceByNameOrTypeOrAreaName(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getDeviceByNameOrTypeOrAreaName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<NetworkDevices> networkDevicesList;
            if (getMvnoIdFromCurrentStaff() == 1)
                networkDevicesList = networkDeviceRepository.findAllByNameContainingIgnoreCaseOrDevicetypeContainingIgnoreCaseOrServicearea_NameContainingIgnoreCase(s1, s1, s1, pageRequest);
            else
                networkDevicesList = networkDeviceRepository.findAllByNameContainingIgnoreCaseOrDevicetypeContainingIgnoreCaseOrServicearea_NameContainingIgnoreCaseAndMvnoIdIn(s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != networkDevicesList && 0 < networkDevicesList.getSize()) {
                makeGenericResponse(genericDataDTO, networkDevicesList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        JPAQuery<?> query = new JPAQuery<>(entityManager);
//        QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
//        QProduct qProduct = QProduct.product;
//        BooleanExpression booleanExpression = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false));
//        if (getMvnoIdFromCurrentStaff() != 1) {
//            booleanExpression = booleanExpression.and(qNetworkDevices.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//        }
//        try {
//            PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (filterList.size() > 0) {
//                for (GenericSearchModel genericSearchModel : filterList) {
//                    String s1 = genericSearchModel.getFilterValue();
//                    if (genericSearchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        if (!genericSearchModel.getFilterValue().isEmpty()) {
//                            booleanExpression = booleanExpression.and((qNetworkDevices.name.likeIgnoreCase("%" + s1 + "%"))
//                                    .or(qNetworkDevices.servicearea.name.likeIgnoreCase("%" + s1 + "%"))
//                                    .or(qNetworkDevices.status.likeIgnoreCase("%" + s1 + "%"))
//                                    .or(qNetworkDevices.devicetype.likeIgnoreCase("%" + s1 + "%")));
//                        }
//                    }
//                    if (null != genericSearchModel.getFilterCondition()) {
//                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("status")) {
//                            booleanExpression = booleanExpression.and(qNetworkDevices.status.likeIgnoreCase("%" + s1 + "%"));
//                        }
//                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("devicetype")) {
//                            booleanExpression = booleanExpression.and(qNetworkDevices.devicetype.likeIgnoreCase("%" + s1 + "%"));
//                        }
//                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("name")) {
//                            booleanExpression = booleanExpression.and(qNetworkDevices.name.likeIgnoreCase("%" + s1 + "%"));
//                        }
//                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("ServiceArea")) {
//                            booleanExpression = booleanExpression.and(qNetworkDevices.servicearea.name.likeIgnoreCase("%" + s1 + "%"));
//                        }
//                        if (genericSearchModel.getFilterColumn().equalsIgnoreCase("Product")) {
//                            booleanExpression = booleanExpression.and(qNetworkDevices.product.name.likeIgnoreCase("%" + s1 + "%"));
//                        }
//                    }
//                }
//                Page<NetworkDevices> networkDevices = networkDeviceRepository.findAll(booleanExpression, pageRequest);
//                if (null != networkDevices && 0 < networkDevices.getSize()) {
//                    return makeGenericResponse(genericDataDTO, networkDevices);
//                }
//                if (networkDevices.getTotalElements() == 0) {
//                    genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                    genericDataDTO.setResponseMessage("Data Not Found.");
//                }
//            }
//        } catch (Exception ex) {
//            throw ex;
//        }
//        return genericDataDTO;
//    }

    /**
     * Search generic data dto.
     *
     * @param filterList the filter list
     * @param page the page
     * @param pageSize the page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the generic data dto
     */
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<NetworkDevices> networkDevices = null;
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel genericSearchModel : filterList) {
                String displayname = genericSearchModel.getFilterValue();
                String filterColumn = genericSearchModel.getFilterColumn();
                if (displayname != "" && filterColumn != "") {
                    if (getMvnoIdFromCurrentStaff() == 1) {
                        if (filterColumn.equalsIgnoreCase("DISPLAY NAME")) {
                            networkDevices = networkDeviceRepository.findAllNativeByDisplayNameLike(displayname, pageRequest);
                        } else {
                            networkDevices = networkDeviceRepository.findAllNativeByDeviceTypeAndDisplayNameLike(filterColumn, displayname, pageRequest);
                        }
                    } else {
                        if (filterColumn.equalsIgnoreCase("DISPLAY NAME")) {
                            networkDevices = networkDeviceRepository.findAllNativeByMvnoIdInAndDisplayNameLike(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), displayname, pageRequest);
                        } else {
                            networkDevices = networkDeviceRepository.findAllNativeByMvnoIdInAndDeviceTypeAndDisplayNameLike(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), filterColumn, displayname, pageRequest);
                        }
                    }
                    if (null != networkDevices && 0 < networkDevices.getSize()) {
                        makeGenericResponse(genericDataDTO, networkDevices);
                    }
                } else {
                    genericDataDTO = getListByPageAndSizeAndSortByAndOrderBy(page, pageSize, sortBy, sortOrder, filterList);
                }
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }

    /**
     * Gets by device name.
     *
     * @param s1 the s 1
     * @param pageRequest the page request
     * @return the by device name
     */
    public GenericDataDTO getByDeviceName(String s1, PageRequest pageRequest) {
        try {
            String SUBMODULE = getModuleNameForLog() + " [getByDeviceName()] ";
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
            BooleanExpression booleanExpression = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false))
                    .and((qNetworkDevices.name.likeIgnoreCase("%" + s1 + "%")).or(qNetworkDevices.serviceAreaNameList.get(0).name.likeIgnoreCase("%" + s1 + "%")).or(qNetworkDevices.status.likeIgnoreCase("%" + s1 + "%")));
            if (getMvnoIdFromCurrentStaff() != 1) {
                booleanExpression = booleanExpression.and(qNetworkDevices.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            }
            Page<NetworkDevices> networkDevices = networkDeviceRepository.findAll(booleanExpression, pageRequest);
            if (null != networkDevices && 0 < networkDevices.getSize()) {
                makeGenericResponse(genericDataDTO, networkDevices);
            }
            if (networkDevices.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }

            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Duplicate verify at save boolean.
     *
     * @param name the name
     * @return the boolean
     * @throws Exception the exception
     */
    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = networkDeviceRepository.duplicateVerifyAtSave(name);
            else
                count = networkDeviceRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Duplicate verify at edit boolean.
     *
     * @param name the name
     * @param id the id
     * @return the boolean
     * @throws Exception the exception
     */
    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = networkDeviceRepository.duplicateVerifyAtEdit(name, id);
            else
                count = networkDeviceRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Delete verification boolean.
     *
     * @param id the id
     * @return the boolean
     * @throws Exception the exception
     */
    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = networkDeviceRepository.deleteVerify(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * Save entity network device dto.
     *
     * @param entity the entity
     * @return the network device dto
     * @throws Exception the exception
     */
    @Override
    public NetworkDeviceDTO saveEntity(NetworkDeviceDTO entity) throws Exception {
        try {
            if (entity.getServiceAreaIdsList() != null) {
                List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllById(entity.getServiceAreaIdsList());
                List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                for (ServiceArea serviceArea : serviceAreaList) {
                    ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext());
                    serviceAreaDTOS.add(serviceAreaDTO);
                }
                entity.setServiceAreaNameList(serviceAreaDTOS);
                if (entity.getTotalInPorts() != null && entity.getTotalInPorts() >= 0 && entity.getTotalOutPorts() != null && entity.getTotalOutPorts() >= 0) {
                    entity.setTotalPorts(entity.getTotalInPorts() + entity.getTotalOutPorts());
                    entity.setAvailablePorts(entity.getTotalPorts());
                }
            }
            return super.saveEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Update entity network device dto.
     *
     * @param entity the entity
     * @return the network device dto
     */
    @Override
    public NetworkDeviceDTO updateEntity(NetworkDeviceDTO entity) {
        try {
            getEntityForUpdateAndDelete(entity.getId());
            if (entity.getServiceAreaIdsList() != null) {
                List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllById(entity.getServiceAreaIdsList());
                List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                for (ServiceArea serviceArea : serviceAreaList) {
                    ServiceAreaDTO serviceAreaDTO = serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext());
                    serviceAreaDTOS.add(serviceAreaDTO);
                }
                entity.setServiceAreaNameList(serviceAreaDTOS);
            }
            return super.updateEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Gets hierarchy.
     *
     * @param id the id
     * @return the hierarchy
     */
    public Map<String, Object> getHierarchy(Long id) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<NetworkDeviceBindDTO> parentList = networkdeviceBindRepository.findByCurrentDeviceId(id)
                    .stream()
                    .map(this::convertMappingToDTOSS)
                    .filter(dto -> dto.getPortType().equalsIgnoreCase("in")) // Filter by portType "in"
                    .collect(Collectors.toList());

            List<NetworkDeviceBindDTO> childList = networkdeviceBindRepository.findByCurrentDeviceId(id)
                    .stream()
                    .map(this::convertMappingToDTOSS)
                    .filter(dto -> dto.getPortType().equalsIgnoreCase("OUT")) // Filter by portType "in"
                    .collect(Collectors.toList());
            map.put("parent", parentList);
            map.put("children", childList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * Gets network device bind by current device id.
     *
     * @param id the id
     * @return the network device bind by current device id
     */
    public List<NetworkDeviceBind> getNetworkDeviceBindByCurrentDeviceId(Long id) {
        try {
            List<NetworkDeviceBind> deviceBindList = new ArrayList<>();
            if (id != null)
                deviceBindList = networkdeviceBindRepository.findByCurrentDeviceId(id);
            deviceBindList.stream().forEach(device -> {
                if (device.getPortType().equalsIgnoreCase("IN")) {
                    List<NetworkDeviceBindDTO> childList = networkdeviceBindRepository.findByCurrentDeviceId(id)
                            .stream()
                            .map(this::convertMappingToDTOSS)
                            .filter(dto -> dto.getPortType().equalsIgnoreCase("OUT"))
                            .collect(Collectors.toList());
                    if (childList != null && childList.isEmpty())
                        device.setCanDelete(true);
                    else
                        device.setCanDelete(false);
                }
                if (device.getPortType().equalsIgnoreCase("OUT")) {
                    List<NetworkDeviceBindDTO> childList = networkdeviceBindRepository.findByCurrentDeviceId(device.getOtherDeviceId())
                            .stream()
                            .map(this::convertMappingToDTOSS)
                            .filter(dto -> dto.getPortType().equalsIgnoreCase("OUT"))
                            .collect(Collectors.toList());
                    if (childList != null && childList.isEmpty())
                        device.setCanDelete(true);
                    else
                        device.setCanDelete(false);
                }
            });
            return deviceBindList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets device hierarchy.
     *
     * @param id the id
     * @return the device hierarchy
     */
    public List<DeviceHierarchy> getDeviceHierarchy(Long id) {
        try {
            List<DeviceHierarchy> partnerHierarchyList = new ArrayList<>();
            List<DeviceHierarchy> hierarchy = new ArrayList<>();
            String parentPartnerName = null;
            String deviceType = null;
            Boolean selectedDeviceFlag = false;
            if (id != null) {
                NetworkDevices device = networkDeviceRepository.findById(id).orElse(null);
                List<NetworkDeviceBindDTO> parentList = networkdeviceBindRepository.findByCurrentDeviceId(id)
                        .stream()
                        .map(this::convertMappingToDTOSS)
                        .filter(dto -> dto.getPortType().equalsIgnoreCase("in")) // Filter by portType "in"
                        .collect(Collectors.toList());

                List<NetworkDeviceBindDTO> parentParentList = parentList;

                while (parentList != null && !parentList.isEmpty() && parentParentList != null && !parentParentList.isEmpty()) {
                    parentParentList = networkdeviceBindRepository.findByCurrentDeviceId(parentList.get(0).getOtherDeviceId())
                            .stream()
                            .map(this::convertMappingToDTOSS)
                            .filter(dto -> dto.getPortType().equalsIgnoreCase("in")) // Filter by portType "in"
                            .collect(Collectors.toList());
                    if (parentParentList != null && !parentParentList.isEmpty())
                        parentList = parentParentList;
                }

                if (parentList != null && !parentList.isEmpty()) {
                    parentPartnerName = parentList.get(0).getDeviceName();
                    NetworkDevices devices = networkDeviceRepository.findById(parentList.get(0).getOtherDeviceId()).orElse(null);
                    if (devices != null)
                        deviceType = devices.getDevicetype();
                    hierarchy = getChildPartnerHierarchyList(parentList.get(0).getOtherDeviceId(), id);
                } else if (parentList != null && parentList.isEmpty() && device != null) {
                    parentPartnerName = device.getName();
                    deviceType = device.getDevicetype();
                    selectedDeviceFlag = true;
                    hierarchy = getChildPartnerHierarchyList(id, id);
                }


                if (parentPartnerName != null) {
                    if (deviceType == null || deviceType.isEmpty())
                        partnerHierarchyList.add(new DeviceHierarchy("Network", true, selectedDeviceFlag ? "bg-indigo-500 text-white selected-node" : "bg-indigo-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/02_OLT_Y2.png", parentPartnerName, "ONT", parentPartnerName), hierarchy));
                    if (deviceType.equalsIgnoreCase("OLT"))
                        partnerHierarchyList.add(new DeviceHierarchy("Network", true, selectedDeviceFlag ? "bg-indigo-500 text-white selected-node" : "bg-indigo-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/02_OLT_Y2.png", parentPartnerName, "OLT", parentPartnerName), hierarchy));
                    if (deviceType.equalsIgnoreCase("Splitter"))
                        partnerHierarchyList.add(new DeviceHierarchy("Network", true, selectedDeviceFlag ? "bg-purple-500 text-white selected-node" : "bg-purple-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png", parentPartnerName, "Splitter", parentPartnerName), hierarchy));
                    if (deviceType.equalsIgnoreCase("ONU"))
                        partnerHierarchyList.add(new DeviceHierarchy("Network", true, selectedDeviceFlag ? "bg-teal-500 text-white selected-node" : "bg-teal-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/01_ONU_Y2.png", parentPartnerName, "ONU", parentPartnerName), hierarchy));
                    if (deviceType.equalsIgnoreCase("Switch"))
                        partnerHierarchyList.add(new DeviceHierarchy("Network", true, selectedDeviceFlag ? "bg-grey-500 text-white selected-node" : "bg-grey-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png", parentPartnerName, "Switch", parentPartnerName), hierarchy));
                    if (deviceType.equalsIgnoreCase("Router"))
                        partnerHierarchyList.add(new DeviceHierarchy("Network", true, selectedDeviceFlag ? "bg-green-500 text-white selected-node" : "bg-green-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png", parentPartnerName, "Router", parentPartnerName), hierarchy));
                    if (deviceType.equalsIgnoreCase("Master DB/DB"))
                        partnerHierarchyList.add(new DeviceHierarchy("Network", true, selectedDeviceFlag ? "bg-orange-500 text-white selected-node" : "bg-orange-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png", parentPartnerName, "Master DB/DB", parentPartnerName), hierarchy));
                } else
                    return hierarchy;
            }
            return partnerHierarchyList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets child partner hierarchy list.
     *
     * @param id the id
     * @param selectedId the selected id
     * @return the child partner hierarchy list
     */
    public List<DeviceHierarchy> getChildPartnerHierarchyList(Long id, Long selectedId) {
        try {
            List<DeviceHierarchy> children = new ArrayList<>();
            if (id != null) {
                List<NetworkDeviceBindDTO> childList = networkdeviceBindRepository.findByCurrentDeviceId(id)
                        .stream()
                        .map(this::convertMappingToDTOSS)
                        .filter(dto -> dto.getPortType().equalsIgnoreCase("OUT"))
                        .collect(Collectors.toList());

                childList.stream().forEach(x -> {
                    NetworkDevices devices = networkDeviceRepository.findById(x.getOtherDeviceId()).orElse(null);
                    if (devices != null) {
                        if (devices.getDevicetype() == null || devices.getDevicetype().isEmpty())
                            children.add(new DeviceHierarchy("Network", true, x.getOtherDeviceId().equals(selectedId) ? "bg-indigo-500 text-white selected-node" : "bg-indigo-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/02_OLT_Y2.png", x.getCurrentDevicePort(), "ONT", x.getCurrentDevicePort()), getChildPartnerHierarchyList(x.getOtherDeviceId(), selectedId)));
                        if (devices.getDevicetype().equalsIgnoreCase("OLT"))
                            children.add(new DeviceHierarchy("Network", true, x.getOtherDeviceId().equals(selectedId) ? "bg-indigo-500 text-white selected-node" : "bg-indigo-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/02_OLT_Y2.png", x.getCurrentDevicePort(), "OLT", x.getCurrentDevicePort()), getChildPartnerHierarchyList(x.getOtherDeviceId(), selectedId)));
                        if (devices.getDevicetype().equalsIgnoreCase("Splitter"))
                            children.add(new DeviceHierarchy("Network", true, x.getOtherDeviceId().equals(selectedId) ? "bg-purple-500 text-white selected-node" : "bg-purple-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png", x.getCurrentDevicePort(), "Splitter", x.getCurrentDevicePort()), getChildPartnerHierarchyList(x.getOtherDeviceId(), selectedId)));
                        if (devices.getDevicetype().equalsIgnoreCase("ONU"))
                            children.add(new DeviceHierarchy("Network", true, x.getOtherDeviceId().equals(selectedId) ? "bg-teal-500 text-white selected-node" : "bg-teal-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/01_ONU_Y2.png", x.getCurrentDevicePort(), "ONU", x.getCurrentDevicePort()), getChildPartnerHierarchyList(x.getOtherDeviceId(), selectedId)));
                        if (devices.getDevicetype().equalsIgnoreCase("Switch"))
                            children.add(new DeviceHierarchy("Network", true, x.getOtherDeviceId().equals(selectedId) ? "bg-grey-500 text-white selected-node" : "bg-grey-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png", x.getCurrentDevicePort(), "Switch", x.getCurrentDevicePort()), getChildPartnerHierarchyList(x.getOtherDeviceId(), selectedId)));
                        if (devices.getDevicetype().equalsIgnoreCase("Router"))
                            children.add(new DeviceHierarchy("Network", true, x.getOtherDeviceId().equals(selectedId) ? "bg-green-500 text-white selected-node" : "bg-green-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png", x.getCurrentDevicePort(), "Router", x.getCurrentDevicePort()), getChildPartnerHierarchyList(x.getOtherDeviceId(), selectedId)));
                        if (devices.getDevicetype().equalsIgnoreCase("Master DB/DB"))
                            children.add(new DeviceHierarchy("Network", true, x.getOtherDeviceId().equals(selectedId) ? "bg-orange-500 text-white selected-node" : "bg-orange-500 text-white", new GraphData("assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png", x.getCurrentDevicePort(), "Master DB/DB", x.getCurrentDevicePort()), getChildPartnerHierarchyList(x.getOtherDeviceId(), selectedId)));
                    }
                });
            }
            return children;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Bind network devices string.
     *
     * @param customerId the customer id
     * @param networkDeviceId the network device id
     * @return the string
     * @throws Exception the exception
     */
    @javax.transaction.Transactional
    public String bindNetworkDevices(Integer customerId, Long networkDeviceId) throws Exception {
        String SUBMODULE = MODULE + " [bindNetworkDevices()] ";
        try {
            Customers customers = customersRepository.findById(customerId).get();//getEntityForUpdateAndDelete(Long.valueOf(customerId));
            if (customers == null) {
                throw new CustomValidationException(APIConstants.FAIL, "Invalid customer for Network Device", null);
            }
            NetworkDeviceDTO oldNetworkDeviceDTO = null;

            // Release port from old Network Device
            if (customers.getNetworkDeviceId() != null) {
                oldNetworkDeviceDTO = getEntityById(Long.valueOf(customers.getNetworkDeviceId()));
                oldNetworkDeviceDTO.setAvailableOutPorts((oldNetworkDeviceDTO.getAvailableOutPorts() == null) || (oldNetworkDeviceDTO.getAvailableOutPorts() == -1) ? -1 : (oldNetworkDeviceDTO.getAvailableOutPorts() + 1));
                updateEntity(oldNetworkDeviceDTO);
            }

            // Add port in new Network Device
            NetworkDeviceDTO newNetworkDeviceDTO = getEntityById(networkDeviceId);
            if (newNetworkDeviceDTO != null) {
                if (newNetworkDeviceDTO.getAvailableOutPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), newNetworkDeviceDTO.getId() + "-" + newNetworkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                newNetworkDeviceDTO.setAvailableOutPorts((newNetworkDeviceDTO.getAvailableOutPorts() == null) || (newNetworkDeviceDTO.getAvailableOutPorts() == -1) ? -1 : (newNetworkDeviceDTO.getAvailableOutPorts() - 1));
                updateEntity(newNetworkDeviceDTO);
            }
            NetworkDevices networkDevices = networkDeviceMapper.dtoToDomain(newNetworkDeviceDTO, new CycleAvoidingMappingContext());

            customers.setNetworkDeviceId(networkDevices.getId().intValue());
            customers.setSplitterid(networkDevices.getId());
            customersRepository.save(customers);
            return "Success : Network device bound with customer";
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error("Unable to bind Networkdevice for customer response{};exception{}", APIConstants.FAIL, ex.getStackTrace());
            throw new CustomValidationException(APIConstants.FAIL, ex.getMessage(), null);
        }
    }

    /**
     * Gets all entities.
     *
     * @return the all entities
     */
    @Override
    public List<NetworkDeviceDTO> getAllEntities() {
        try {
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
            QNetworkDeviceServiceAreaMapping qNetworkDeviceServiceAreaMapping = QNetworkDeviceServiceAreaMapping.networkDeviceServiceAreaMapping;
            BooleanExpression aBoolean = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false));
            if (getLoggedInUserId() != 1) {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceIDs = serviceAreaService.getServiceAreaByStaffId();
                if (!CollectionUtils.isEmpty(serviceIDs))
                    aBoolean = aBoolean.and(qNetworkDevices.id.in(query.select(qNetworkDeviceServiceAreaMapping.deviceId).from(qNetworkDeviceServiceAreaMapping)
                            .where(qNetworkDeviceServiceAreaMapping.serviceIdList.in(serviceIDs))));
            }
            List<NetworkDevices> networkDevicesList = IterableUtils.toList(networkDeviceRepository.findAll(aBoolean));
            return networkDevicesList.stream().map(data -> super.getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(networkDeviceDTO -> networkDeviceDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || networkDeviceDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Gets list by page and size and sort by and order by.
     *
     * @param page the page
     * @param size the size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param filterList the filter list
     * @return the list by page and size and sort by and order by
     */
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<NetworkDevices> paginationList = null;
            PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);

            JPAQuery<?> query = new JPAQuery<>(entityManager);
            QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
            QNetworkDeviceServiceAreaMapping qNetworkDeviceServiceAreaMapping = QNetworkDeviceServiceAreaMapping.networkDeviceServiceAreaMapping;
            BooleanExpression aBoolean = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false));
            if (getLoggedInUserId() != 1) {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceIDs = serviceAreaService.getServiceAreaByStaffId();
                if (!CollectionUtils.isEmpty(serviceIDs))
                    aBoolean = aBoolean.and(qNetworkDevices.id.in(query.select(qNetworkDeviceServiceAreaMapping.deviceId).from(qNetworkDeviceServiceAreaMapping)
                            .where(qNetworkDeviceServiceAreaMapping.serviceIdList.in(serviceIDs))));

                if (getMvnoIdFromCurrentStaff() != 1)
                    aBoolean = aBoolean.and(qNetworkDevices.mvnoId.in(Arrays.asList(getMvnoIdFromCurrentStaff(), 1)));

            }
            paginationList = networkDeviceRepository.findAll(aBoolean, pageRequest);

            if (null != paginationList && 0 < paginationList.getContent().size())
                makeGenericResponse(genericDataDTO, paginationList);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets entity to update.
     *
     * @param id the id
     * @return the entity to update
     */
    public NetworkDevices getEntityToUpdate(Long id) {
        if (getMvnoIdFromCurrentStaff() == 1)
            return networkDeviceRepository.getOne(id);
        else
            return networkDeviceRepository.findByIdAndMvnoIdIn(id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
    }

    /**
     * Save parent device bindings list.
     *
     * @param deviceMappingDTO the device mapping dto
     * @return the list
     */
    @Transactional
    public List<NetworkDeviceBindingsDTO> saveParentDeviceBindings(DeviceMappingDTO deviceMappingDTO) {
        try {
//      Port type - IN
            Set<Long> inPortParentIds = boundParents(deviceMappingDTO.getDeviceId()).stream().filter(networkDeviceBindingsDTO -> networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.IN)).map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toSet());

            Set<Long> inDBSimilar = new HashSet<>(inPortParentIds);
            Set<Long> inDBDifferent = new HashSet<>(inPortParentIds);
            Set<Long> inNewDifferent = new HashSet<>(deviceMappingDTO.getInPortDevices());

            inDBSimilar.retainAll(deviceMappingDTO.getInPortDevices());
            inDBDifferent.removeAll(inDBSimilar);
            inNewDifferent.removeAll(inDBSimilar);

            // (+1) Release 'out ports' from inDBDifferent, They are the ids of devices which are replaced by others
            for (Long deviceId : inDBDifferent) {
                NetworkDeviceDTO networkDeviceDTO = getEntityById(deviceId);
                networkDeviceDTO.setAvailableOutPorts(networkDeviceDTO.getAvailableOutPorts() + 1);
                saveEntity(networkDeviceDTO);
            }
            // Delete overriden mappings
            networkDeviceBindingsRepository.deleteByDeviceIdAndParentDeviceIdIn(deviceMappingDTO.getDeviceId(), inDBDifferent);

            // (-1) Bind 'out ports' of inNewDifferent, They are the new devices & devices who replaced existing devices
            for (Long deviceId : inNewDifferent) {
                NetworkDeviceDTO networkDeviceDTO = getEntityById(deviceId);
                if (networkDeviceDTO.getAvailableOutPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), networkDeviceDTO.getId() + "-" + networkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                networkDeviceDTO.setAvailableOutPorts(networkDeviceDTO.getAvailableOutPorts() - 1);
                saveEntity(networkDeviceDTO);

                // Save new mapping
                NetworkDeviceBindings networkDeviceBindings = new NetworkDeviceBindings(deviceMappingDTO.getDeviceId(), CommonConstants.IN, deviceId);
                networkDeviceBindingsRepository.save(networkDeviceBindings);
            }

//      Port type - OUT
            Set<Long> outPortParentIds = boundParents(deviceMappingDTO.getDeviceId()).stream().filter(networkDeviceBindingsDTO -> networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.OUT)).map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toSet());

            Set<Long> outDBSimilar = new HashSet<>(outPortParentIds);
            Set<Long> outDBDifferent = new HashSet<>(outPortParentIds);
            Set<Long> outNewDifferent = new HashSet<>(deviceMappingDTO.getOutPortDevices());

            outDBSimilar.retainAll(deviceMappingDTO.getOutPortDevices());
            outDBDifferent.removeAll(outDBSimilar);
            outNewDifferent.removeAll(outDBSimilar);

            // (+1) Release 'in ports' from outDBDifferent, They are the ids of devices which are replaced by others
            for (Long deviceId : outDBDifferent) {
                NetworkDeviceDTO networkDeviceDTO = getEntityById(deviceId);
                networkDeviceDTO.setAvailableInPorts(networkDeviceDTO.getAvailableInPorts() + 1);
                saveEntity(networkDeviceDTO);
            }
            // Delete overriden mappings
            networkDeviceBindingsRepository.deleteByDeviceIdAndParentDeviceIdIn(deviceMappingDTO.getDeviceId(), outDBDifferent);

            // (-1) Bind 'in ports' of outNewDifferent, They are the new devices & devices who replaced existing devices
            for (Long deviceId : outNewDifferent) {
                NetworkDeviceDTO networkDeviceDTO = getEntityById(deviceId);
                if (networkDeviceDTO.getAvailableInPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), networkDeviceDTO.getId() + "-" + networkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                networkDeviceDTO.setAvailableInPorts(networkDeviceDTO.getAvailableInPorts() - 1);
                saveEntity(networkDeviceDTO);

                // Save new mapping
                NetworkDeviceBindings networkDeviceBindings = new NetworkDeviceBindings(deviceMappingDTO.getDeviceId(), CommonConstants.OUT, deviceId);
                networkDeviceBindingsRepository.save(networkDeviceBindings);
            }

//  Update IN/OUT ports from current device
            NetworkDeviceDTO currentDevice = getEntityById(deviceMappingDTO.getDeviceId());
            currentDevice.setAvailableInPorts(currentDevice.getAvailableInPorts() + inDBDifferent.size() - inNewDifferent.size());
            currentDevice.setAvailableOutPorts(currentDevice.getAvailableOutPorts() + outDBDifferent.size() - outNewDifferent.size());
            saveEntity(currentDevice);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return boundParents(deviceMappingDTO.getDeviceId());
    }

    /**
     * Device child parent binding list.
     *
     * @param devicePortsBindings the device ports bindings
     * @return the list
     */
    @Transactional
    public List<NetworkDeviceBindingsDTO> deviceChildParentBinding(DevicePortMappingDTO devicePortsBindings) {
        try {
            List<Long> devicesBindedToINPort = devicePortsBindings.getInPortDevices().stream().map(NetworkDevicePortsBinding::getParentDeviceId).collect(Collectors.toList());
            List<Long> devicesBindedToOUTPort = devicePortsBindings.getOutPortDevices().stream().map(NetworkDevicePortsBinding::getParentDeviceId).collect(Collectors.toList());
//      Port type - IN
            Set<Long> inPortParentIds = boundParents(devicePortsBindings.getDeviceId()).stream().filter(networkDeviceBindingsDTO -> networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.IN)).map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toSet());

            Set<Long> inDBSimilar = new HashSet<>(inPortParentIds);
            Set<Long> inDBDifferent = new HashSet<>(inPortParentIds);
            Set<Long> inNewDifferent = new HashSet<>(devicesBindedToINPort);

            inDBSimilar.retainAll(devicesBindedToINPort);
            inDBDifferent.removeAll(inDBSimilar);
            inNewDifferent.removeAll(inDBSimilar);

            // (+1) Release 'out ports' from inDBDifferent, They are the ids of devices which are replaced by others
            for (Long deviceId : inDBDifferent) {
                NetworkDeviceDTO networkDeviceDTO = getEntityById(deviceId);
                networkDeviceDTO.setAvailableOutPorts(networkDeviceDTO.getAvailableOutPorts() + 1);
                saveEntity(networkDeviceDTO);
            }
            // Delete overriden mappings
            networkDeviceBindingsRepository.deleteByDeviceIdAndParentDeviceIdIn(devicePortsBindings.getDeviceId(), inDBDifferent);

            // (-1) Bind 'out ports' of inNewDifferent, They are the new devices & devices who replaced existing devices
            for (Long deviceId : inNewDifferent) {
                NetworkDeviceDTO networkDeviceDTO = getEntityById(deviceId);
                if (networkDeviceDTO.getAvailableOutPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), networkDeviceDTO.getId() + "-" + networkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                networkDeviceDTO.setAvailableOutPorts(networkDeviceDTO.getAvailableOutPorts() - 1);
                saveEntity(networkDeviceDTO);

                // Here check inbind and outbind duplicates(duplicates should not enter coz 1 port has 1 device attached)
                List<NetworkDevicePortsBinding> networkDevicePortsBindings = devicePortsBindings.getInPortDevices().stream().filter(devicePortsBinding -> devicePortsBinding.getParentDeviceId() == deviceId).collect(Collectors.toList());
                Boolean isDeviceBinded = false;
                isDeviceBinded = isPortAvailable(devicePortsBindings.getDeviceId(), networkDevicePortsBindings.get(0).getInBind(), CommonConstants.IN);
                if (isDeviceBinded) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.PORT_OCCUPIED, null);
                }
                isDeviceBinded = isPortAvailable(deviceId, networkDevicePortsBindings.get(0).getOutBind(), CommonConstants.OUT);
                if (isDeviceBinded) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.PORT_OCCUPIED, null);
                }

                // Save new mapping
                NetworkDeviceBindings networkDeviceBindings = new NetworkDeviceBindings(devicePortsBindings.getDeviceId(), CommonConstants.IN, deviceId, networkDevicePortsBindings.get(0).getInBind(), networkDevicePortsBindings.get(0).getOutBind());
                networkDeviceBindingsRepository.save(networkDeviceBindings);
            }

//      Port type - OUT
            Set<Long> outPortParentIds = boundParents(devicePortsBindings.getDeviceId()).stream().filter(networkDeviceBindingsDTO -> networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.OUT)).map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toSet());

            Set<Long> outDBSimilar = new HashSet<>(outPortParentIds);
            Set<Long> outDBDifferent = new HashSet<>(outPortParentIds);
            Set<Long> outNewDifferent = new HashSet<>(devicesBindedToOUTPort);

            outDBSimilar.retainAll(devicesBindedToOUTPort);
            outDBDifferent.removeAll(outDBSimilar);
            outNewDifferent.removeAll(outDBSimilar);

            // (+1) Release 'in ports' from outDBDifferent, They are the ids of devices which are replaced by others
            for (Long deviceId : outDBDifferent) {
                NetworkDeviceDTO networkDeviceDTO = getEntityById(deviceId);
                networkDeviceDTO.setAvailableInPorts(networkDeviceDTO.getAvailableInPorts() + 1);
                saveEntity(networkDeviceDTO);
            }
            // Delete overriden mappings
            networkDeviceBindingsRepository.deleteByDeviceIdAndParentDeviceIdIn(devicePortsBindings.getDeviceId(), outDBDifferent);

            // (-1) Bind 'in ports' of outNewDifferent, They are the new devices & devices who replaced existing devices
            for (Long deviceId : outNewDifferent) {
                NetworkDeviceDTO networkDeviceDTO = getEntityById(deviceId);
                if (networkDeviceDTO.getAvailableInPorts() == 0)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), networkDeviceDTO.getId() + "-" + networkDeviceDTO.getName() + MessageConstants.PORTS_UNAVAILABLE, null);
                networkDeviceDTO.setAvailableInPorts(networkDeviceDTO.getAvailableInPorts() - 1);
                saveEntity(networkDeviceDTO);

                // Here check inbind and outbind duplicates(duplicates should not enter coz 1 port has 1 device attached)
                List<NetworkDevicePortsBinding> networkDevicePortsBindings = devicePortsBindings.getOutPortDevices().stream().filter(devicePortsBinding -> devicePortsBinding.getParentDeviceId() == deviceId).collect(Collectors.toList());
                Boolean isDeviceBinded = false;
//                isDeviceBinded = isPortAvailable(devicePortsBindings.getDeviceId(), networkDevicePortsBindings.get(0).getOutBind(), CommonConstants.OUT);
                isDeviceBinded = isPortAvailable(devicePortsBindings.getDeviceId(), networkDevicePortsBindings.get(0).getInBind(), CommonConstants.IN);
                if (isDeviceBinded)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.PORT_OCCUPIED, null);
                isDeviceBinded = isPortAvailable(deviceId, networkDevicePortsBindings.get(0).getOutBind(), CommonConstants.OUT);
                if (isDeviceBinded)
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.PORT_OCCUPIED, null);

                // Save new mapping
                NetworkDeviceBindings networkDeviceBindings = new NetworkDeviceBindings(devicePortsBindings.getDeviceId(), CommonConstants.OUT, deviceId, networkDevicePortsBindings.get(0).getInBind(), networkDevicePortsBindings.get(0).getOutBind());
                networkDeviceBindingsRepository.save(networkDeviceBindings);
            }

//  Update IN/OUT ports from current device
            NetworkDeviceDTO currentDevice = getEntityById(devicePortsBindings.getDeviceId());
            currentDevice.setAvailableInPorts(currentDevice.getAvailableInPorts() + inDBDifferent.size() - inNewDifferent.size());
            currentDevice.setAvailableOutPorts(currentDevice.getAvailableOutPorts() + outDBDifferent.size() - outNewDifferent.size());
            saveEntity(currentDevice);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return boundParents(devicePortsBindings.getDeviceId());
    }

    /**
     * Is port available boolean.
     *
     * @param deviceId the device id
     * @param portName the port name
     * @param portType the port type
     * @return the boolean
     */
    private boolean isPortAvailable(Long deviceId, String portName, String portType) {
        NetworkDeviceBindings networkDeviceBindings = null;
        if (portType.equalsIgnoreCase(CommonConstants.IN))
            networkDeviceBindings = networkDeviceBindingsRepository.findByDeviceIdAndInBind(deviceId, portName);
        else
            networkDeviceBindings = networkDeviceBindingsRepository.findByParentDeviceIdAndOutBind(deviceId, portName);
        if (networkDeviceBindings != null)
            return true;
        return false;
    }

    /**
     * Bound parents list.
     *
     * @param id the id
     * @return the list
     */
    public List<NetworkDeviceBindingsDTO> boundParents(Long id) {
        try {
            List<NetworkDeviceBindingsDTO> networkDeviceBindingsList = new ArrayList<>();
            networkDeviceBindingsList.addAll(networkDeviceBindingsRepository.findByDeviceId(id).stream().map(this::convertMappingToDTO).collect(Collectors.toList()));
            List<NetworkDeviceBindingsDTO> parents = networkDeviceBindingsRepository.findByParentDeviceId(id).stream().map(this::convertMappingToDTO).collect(Collectors.toList());
            List<NetworkDeviceBindingsDTO> reversedParents = new ArrayList<>();
            for (NetworkDeviceBindingsDTO networkDeviceBindingsDTO : parents) {
                NetworkDeviceBindingsDTO reversedParent = new NetworkDeviceBindingsDTO();
                reversedParent.setId(networkDeviceBindingsDTO.getId());
                reversedParent.setPortType(networkDeviceBindingsDTO.getPortType().equalsIgnoreCase(CommonConstants.IN) ? CommonConstants.OUT : CommonConstants.IN);
                reversedParent.setOutBind(networkDeviceBindingsDTO.getInBind());
                reversedParent.setInBind(networkDeviceBindingsDTO.getOutBind());
                reversedParent.setDeviceId(networkDeviceBindingsDTO.getParentDeviceId());
                reversedParent.setParentDeviceId(networkDeviceBindingsDTO.getDeviceId());
                reversedParent.setDeviceName(networkDeviceBindingsDTO.getParentDeviceName());
                reversedParent.setParentDeviceName(networkDeviceBindingsDTO.getDeviceName());
                reversedParents.add(reversedParent);
            }
            networkDeviceBindingsList.addAll(reversedParents);
//        networkDeviceBindingsList.addAll(networkDeviceBindingsRepository.findByParentDeviceId(id).stream(c).map(this::convertMappingToDTO).collect(Collectors.toList()));
            return networkDeviceBindingsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Available parents list.
     *
     * @param id the id
     * @return the list
     */
    public List<NetworkDeviceDTO> availableParents(Long id) {
        List<NetworkDeviceDTO> networkDevices = getAllEntities().stream().filter(networkDeviceDTO -> networkDeviceDTO.getId() != id).collect(Collectors.toList());
        List<Long> parentIds = boundParents(id).stream().map(NetworkDeviceBindingsDTO::getParentDeviceId).collect(Collectors.toList());
        List<Long> childIds = boundParents(id).stream().map(NetworkDeviceBindingsDTO::getDeviceId).collect(Collectors.toList());
        // Ignores already mapped entries
        networkDevices = networkDevices.stream().filter(networkDevice -> !parentIds.contains(networkDevice.getId()))
                .filter(networkDevice -> !childIds.contains(networkDevice.getId())).collect(Collectors.toList());
        return networkDevices;
    }

    /**
     * Delete device mapping string.
     *
     * @param id the id
     * @return the string
     */
    public String deleteDeviceMapping(Long id) {
        try {
            networkDeviceBindingsRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Device mapping deleted successfully.";
    }

    /**
     * Delete network device bind by id string.
     *
     * @param id the id
     * @return the string
     */
    public String deleteNetworkDeviceBindById(Long id) {
        try {
            Optional<NetworkDeviceBind> deviceBind = networkdeviceBindRepository.findById(id);
            if (deviceBind.isPresent()) {
                List<NetworkDeviceBind> deviceBinds = networkdeviceBindRepository.findByMappingId(deviceBind.get().getMappingId());
                deviceBinds.stream().forEach(x -> {
                    Optional<NetworkDevices> device = networkDeviceRepository.findById(x.getCurrentDeviceId());
                    if (device.isPresent()) {
                        if (x.getPortType().equalsIgnoreCase("IN")) {

                            if (device.get().getAvailableInPorts() != null)
                                device.get().setAvailableInPorts(device.get().getAvailableInPorts() + 1);
                            else
                                device.get().setAvailableInPorts(1);

                            if (device.get().getAvailablePorts() != null)
                                device.get().setAvailablePorts(device.get().getAvailablePorts() + 1);
                            else
                                device.get().setAvailablePorts(1);
                        }

                        if (x.getPortType().equalsIgnoreCase("OUT")) {

                            if (device.get().getAvailableOutPorts() != null)
                                device.get().setAvailableOutPorts(device.get().getAvailableOutPorts() + 1);
                            else
                                device.get().setAvailableOutPorts(1);

                            if (device.get().getAvailablePorts() != null)
                                device.get().setAvailablePorts(device.get().getAvailablePorts() + 1);
                            else
                                device.get().setAvailablePorts(1);
                        }
                        networkDeviceRepository.save(device.get());
                    }
                    networkdeviceBindRepository.delete(x);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Network Device Binding deleted successfully.";
    }


    /**
     * Change network device binding string.
     *
     * @param bindDTO the bind dto
     * @return the string
     */
    public String changeNetworkDeviceBinding(NetworkDeviceBindDTO bindDTO) {
        try {
            Optional<NetworkDeviceBind> deviceBind = networkdeviceBindRepository.findById(bindDTO.getId());
            if (deviceBind.isPresent()) {
                List<NetworkDeviceBind> deviceBinds = networkdeviceBindRepository.findByMappingId(deviceBind.get().getMappingId());
                deviceBinds.stream().forEach(networkDeviceBind -> {
                    if (networkDeviceBind != null && networkDeviceBind.getCurrentDeviceId().equals(bindDTO.getCurrentDeviceId())) {
                        networkDeviceBind.setCurrentDevicePort(bindDTO.getCurrentDevicePort());
                        networkDeviceBind.setOtherDevicePort(bindDTO.getOtherDevicePort());
                        String currentDeviceType = networkDeviceRepository.findDeviceTypeById(bindDTO.getCurrentDeviceId());
                        String otherDeviceType = networkDeviceRepository.findDeviceTypeById(bindDTO.getOtherDeviceId());
                        Long currentProductId = networkDeviceRepository.findProductIdById(bindDTO.getCurrentDeviceId());
                        Long otherProductId = networkDeviceRepository.findProductIdById(bindDTO.getOtherDeviceId());
                        String currentProductName = productRepository.findProductNameByProductId(currentProductId);
                        String otherProductName = productRepository.findProductNameByProductId(otherProductId);
                        // For current device
                        String[] currentParts = bindDTO.getCurrentDevicePort().split("Port", 2);
                        String setCurrentPortNumber = "Port" + currentParts[1];
                        // For other device
                        String[] otherParts = bindDTO.getOtherDevicePort().split("Port", 2);
                        String setOtherPortNumber = "Port" + otherParts[1];

                        networkDeviceBind.setCurrentDevice(currentProductName);
                        networkDeviceBind.setOtherDevice(otherProductName);
                        networkDeviceBind.setCurrentDevicePortNumber(setCurrentPortNumber);
                        networkDeviceBind.setOtherDevicePortNumber(setOtherPortNumber);
                        networkDeviceBind.setCurrentDeviceType(currentDeviceType);
                        networkDeviceBind.setOtherDeviceType(otherDeviceType);
                        networkdeviceBindRepository.save(networkDeviceBind);
                    }

                    if (networkDeviceBind != null && networkDeviceBind.getCurrentDeviceId().equals(bindDTO.getOtherDeviceId())) {
                        String currentDeviceType = networkDeviceRepository.findDeviceTypeById(bindDTO.getCurrentDeviceId());
                        String otherDeviceType = networkDeviceRepository.findDeviceTypeById(bindDTO.getOtherDeviceId());
                        Long currentProductId = networkDeviceRepository.findProductIdById(bindDTO.getCurrentDeviceId());
                        Long otherProductId = networkDeviceRepository.findProductIdById(bindDTO.getOtherDeviceId());
                        String currentProductName = productRepository.findProductNameByProductId(currentProductId);
                        String otherProductName = productRepository.findProductNameByProductId(otherProductId);
                        // For current device
                        String[] currentParts = bindDTO.getCurrentDevicePort().split("Port", 2);
                        String setCurrentPortNumber = "Port" + currentParts[1];
                        // For other device
                        String[] otherParts = bindDTO.getOtherDevicePort().split("Port", 2);
                        String setOtherPortNumber = "Port" + otherParts[1];

                        networkDeviceBind.setCurrentDevice(otherProductName);
                        networkDeviceBind.setOtherDevice(currentProductName);
                        networkDeviceBind.setCurrentDevicePortNumber(setOtherPortNumber);
                        networkDeviceBind.setOtherDevicePortNumber(setCurrentPortNumber);
                        networkDeviceBind.setCurrentDeviceType(otherDeviceType);
                        networkDeviceBind.setOtherDeviceType(currentDeviceType);
                        networkDeviceBind.setCurrentDevicePort(bindDTO.getOtherDevicePort());
                        networkDeviceBind.setOtherDevicePort(bindDTO.getCurrentDevicePort());
                        networkdeviceBindRepository.save(networkDeviceBind);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Network Device Binding change successfully.";
    }

    /**
     * Convert mapping to dto network device bindings dto.
     *
     * @param networkDeviceBindings the network device bindings
     * @return the network device bindings dto
     */
    NetworkDeviceBindingsDTO convertMappingToDTO(NetworkDeviceBindings networkDeviceBindings) {
        try {
            NetworkDeviceBindingsDTO networkDeviceBindingsDTO = new NetworkDeviceBindingsDTO();
            networkDeviceBindingsDTO.setId(networkDeviceBindings.getId());
            networkDeviceBindingsDTO.setDeviceId(networkDeviceBindings.getDeviceId());
            networkDeviceBindingsDTO.setDeviceName(networkDeviceRepository.findById(networkDeviceBindings.getDeviceId()).get().getName());
            networkDeviceBindingsDTO.setParentDeviceId(networkDeviceBindings.getParentDeviceId());
            networkDeviceBindingsDTO.setParentDeviceName(networkDeviceRepository.findById(networkDeviceBindings.getParentDeviceId()).get().getName());
            networkDeviceBindingsDTO.setPortType(networkDeviceBindings.getPortType());
            networkDeviceBindingsDTO.setInBind(networkDeviceBindings.getInBind());
            networkDeviceBindingsDTO.setOutBind(networkDeviceBindings.getOutBind());
            return networkDeviceBindingsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets ports availability.
     *
     * @param parentDeviceId the parent device id
     * @return the ports availability
     */
    public Set<String> getPortsAvailability(Long parentDeviceId) {
        Set<String> ports = new HashSet<>();
        try {
            NetworkDeviceDTO networkDeviceDTO = getEntityById(parentDeviceId);
            Integer totalInputPorts;
            Integer totalOutputPorts;

            if (networkDeviceDTO != null && networkDeviceDTO.getAvailablePorts() != null) {
                totalInputPorts = networkDeviceDTO.getTotalInPorts();
                totalOutputPorts = networkDeviceDTO.getTotalOutPorts();
                for (int i = 1; i <= totalInputPorts; i++)
                    ports.add(networkDeviceDTO.getName() + "-IN-Port-" + i);
                for (int i = 1; i <= totalOutputPorts; i++)
                    ports.add(networkDeviceDTO.getName() + "-OUT-Port-" + i);
                List<String> UsedPorts = networkdeviceBindRepository.findByCurrentDeviceId(parentDeviceId).stream().map(x -> x.getCurrentDevicePort()).collect(Collectors.toList());
                ports.removeAll(UsedPorts);
                UsedPorts = networkdeviceBindRepository.findByOtherDeviceId(parentDeviceId).stream().map(x -> x.getOtherDevicePort()).collect(Collectors.toList());
                ports.removeAll(UsedPorts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ports;
    }

    /**
     * Gets all inward by product.
     *
     * @param productId the product id
     * @return the all inward by product
     */
    public List<Inward> getAllInwardByProduct(Long productId) {
        try {
            QInward qInward = QInward.inward;
            JPAQuery<Inward> query = new JPAQuery<>(entityManager);
            List<Inward> inwardList = new ArrayList<>();
            BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.productId.id.eq(productId)).and(qInward.sourceType.equalsIgnoreCase(CommonConstants.STAFF).or(qInward.sourceType.equalsIgnoreCase(CommonConstants.PARTNER)))
                    .and(qInward.isDeleted.eq(false)).and((qInward.productId.productCategory.type.eq(CommonConstants.CUSTOMER_BIND)
                            .or(qInward.productId.productCategory.type.eq(CommonConstants.NETWORK_BIND))));
            List<Tuple> result = query.select(qInward.id, qInward.inwardNumber, qInward.unusedQty, qInward.mvnoId).from(qInward).where(booleanExpression).fetch();
            if (!result.isEmpty()) {
                result.forEach(tuple -> {
                    Inward inward = new Inward();
                    inward.setId(tuple.get(qInward.id));
                    inward.setInwardNumber(tuple.get(qInward.inwardNumber));
                    inward.setUnusedQty(tuple.get(qInward.unusedQty));
                    inward.setMvnoId(tuple.get(qInward.mvnoId));
                    inwardList.add(inward);
                });
            }
            if (getMvnoIdFromCurrentStaff() == 1)
                return inwardList;
            else
                return inwardList.stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Search network devices generic data dto.
     *
     * @param pageNumber the page number
     * @param customPageSize the custom page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param searchNetworkDevicesPojo the search network devices pojo
     * @return the generic data dto
     */
    public GenericDataDTO searchNetworkDevices(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchNetworkDevicesPojo searchNetworkDevicesPojo) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (searchNetworkDevicesPojo != null) {
                genericDataDTO = findNetworkDevices(pageNumber, customPageSize, sortBy, sortOrder, searchNetworkDevicesPojo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    /**
     * Find network devices by type generic data dto.
     *
     * @param deviceType the device type
     * @return the generic data dto
     */
    public GenericDataDTO findNetworkDevicesByType(String deviceType) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<NetworkDeviceDTO> networkDeviceDTOList = new ArrayList<>();
        try {
            List<NetworkDevices> networkDevices = networkDeviceRepository.findAllByIsDeletedFalseAndDevicetypeAndStatus(deviceType, "Active");
            if (getMvnoIdFromCurrentStaff() != 1)
                networkDevices = networkDeviceRepository.findAllByIsDeletedFalseAndDevicetypeAndMvnoIdInAndStatus(deviceType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), "Active");

            List<NetworkDeviceDTO> dto = networkDevices.stream().map(networkDevice -> networkDeviceMapper.domainToDTO(networkDevice, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            if (networkDevices.size() != 0) {
                for (NetworkDeviceDTO networkDeviceDTO : dto) {
                    if (networkDeviceDTO.getProductId() != null) {
                        networkDeviceDTO.setProductName(productRepository.findProductNameByProductId(networkDeviceDTO.getProductId()));
                        networkDeviceDTOList.add(networkDeviceDTO);
                    }
                }
            }
            if (networkDeviceDTOList.size() != 0) {
                genericDataDTO.setDataList(networkDeviceDTOList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    /**
     * Find network devices generic data dto.
     *
     * @param pageNumber the page number
     * @param customPageSize the custom page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param search the search
     * @return the generic data dto
     * @throws Exception the exception
     */
    public GenericDataDTO findNetworkDevices(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchNetworkDevicesPojo search) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<NetworkDevices> paginationList = null;
        QNetworkDevices qNetworkDevices = QNetworkDevices.networkDevices;
        List<NetworkDeviceDTO> networkDeviceDTOList = new ArrayList<>();
        BooleanExpression booleanExpression = qNetworkDevices.isNotNull().and(qNetworkDevices.isDeleted.eq(false));
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qNetworkDevices.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            if (search.getDevicetype() != null && !"null".equals(search.getDevicetype()) && !"".equals(search.getDevicetype())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.devicetype.startsWithIgnoreCase(search.getDevicetype()));
            }
            if (search.getName() != null && !"null".equals(search.getName()) && !"".equals(search.getName())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.name.startsWithIgnoreCase(search.getName()));
            }
            if (search.getProductName() != null && !"null".equals(search.getProductName()) && !"".equals(search.getProductName())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.product.name.startsWithIgnoreCase(search.getProductName()));
            }
            if (search.getStatus() != null && !"null".equals(search.getStatus()) && !"".equals(search.getStatus())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.status.startsWithIgnoreCase(search.getStatus()));
            }
            if (search.getServiceName() != null && !"null".equals(search.getServiceName()) && !"".equals(search.getServiceName())) {
                booleanExpression = booleanExpression.and(qNetworkDevices.serviceAreaNameList.get(0).name.startsWithIgnoreCase(search.getServiceName()));
            }
            paginationList = networkDeviceRepository.findAll(booleanExpression, pageRequest);
            List<NetworkDeviceDTO> dto = paginationList.get().map(networkDevices -> networkDeviceMapper.domainToDTO(networkDevices, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            for (NetworkDeviceDTO networkDeviceDTO : dto) {
                if (networkDeviceDTO.getProductId() != null) {
                    networkDeviceDTO.setProductName(productRepository.findById(networkDeviceDTO.getProductId()).get().getName());
                    networkDeviceDTOList.add(networkDeviceDTO);
                }
            }
            if (null != paginationList && 0 < paginationList.getSize()) {
                genericDataDTO.setDataList(networkDeviceDTOList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(paginationList.getTotalElements());
                genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
                genericDataDTO.setTotalPages(paginationList.getTotalPages());
            } else if (paginationList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    /**
     * Save networks network device bind.
     *
     * @param dataStoreMappingDto the data store mapping dto
     * @return the network device bind
     */
    @Transactional
    public NetworkDeviceBind saveNetworks(NetworkDeviceBindDTO dataStoreMappingDto) {
        try {
            String currentDeviceType = networkDeviceRepository.findDeviceTypeById(dataStoreMappingDto.getCurrentDeviceId());
            String otherDeviceType = networkDeviceRepository.findDeviceTypeById(dataStoreMappingDto.getOtherDeviceId());
            Long currentProductId = networkDeviceRepository.findProductIdById(dataStoreMappingDto.getCurrentDeviceId());
            Long otherProductId = networkDeviceRepository.findProductIdById(dataStoreMappingDto.getOtherDeviceId());
            String currentProductName = productRepository.findProductNameByProductId(currentProductId);
            String otherProductName = productRepository.findProductNameByProductId(otherProductId);
            // For current device
            String[] currentParts = dataStoreMappingDto.getCurrentDevicePort().split("Port", 2);
            String setCurrentPortNumber = "Port" + currentParts[1];
            // For other device
            String[] otherParts = dataStoreMappingDto.getOtherDevicePort().split("Port", 2);
            String setOtherPortNumber = "Port" + otherParts[1];
            NetworkDeviceBind currentDeviceMapping = getCurrentDeviceMapping(dataStoreMappingDto, currentDeviceType, currentProductName, setCurrentPortNumber, otherDeviceType, otherProductName, setOtherPortNumber);
            NetworkDeviceBind otherDeviceMapping = getOtherDeviceMapping(dataStoreMappingDto, currentDeviceMapping, currentDeviceType, currentProductName, setCurrentPortNumber, otherDeviceType, otherProductName, setOtherPortNumber);
            saveCurrentNetworkDevice(currentDeviceMapping);
            saveOtherNetworkDevice(otherDeviceMapping, currentDeviceMapping);
            NetworkDeviceBind networkDeviceBind = networkdeviceBindRepository.findTopByOrderByIdDesc();
            if (networkDeviceBind == null) {
                currentDeviceMapping.setMappingId(1);
                otherDeviceMapping.setMappingId(1);
                networkdeviceBindRepository.save(currentDeviceMapping);
                networkdeviceBindRepository.save(otherDeviceMapping);
            } else {
                currentDeviceMapping.setMappingId(networkDeviceBind.getMappingId() + 1);
                otherDeviceMapping.setMappingId(networkDeviceBind.getMappingId() + 1);
                networkdeviceBindRepository.save(currentDeviceMapping);
                networkdeviceBindRepository.save(otherDeviceMapping);
            }
            return currentDeviceMapping;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Save other network device.
     *
     * @param otherDeviceMapping the other device mapping
     * @param currentDeviceMapping the current device mapping
     */
    private void saveOtherNetworkDevice(NetworkDeviceBind otherDeviceMapping, NetworkDeviceBind currentDeviceMapping) {
        Optional<NetworkDevices> otherDevice = networkDeviceRepository.findById(currentDeviceMapping.getOtherDeviceId());
        otherDevice.ifPresent(device -> {
            int availablePorts = device.getAvailablePorts();
            int newAvailablePorts = Math.max(availablePorts - 1, 1);
            device.setAvailablePorts(newAvailablePorts);
            if (otherDeviceMapping.getPortType().equalsIgnoreCase("IN")) {
                if (device.getAvailableInPorts() != null)
                    device.setAvailableInPorts(device.getAvailableInPorts() - 1);
                else
                    device.setAvailableInPorts(0);
            }
            if (otherDeviceMapping.getPortType().equalsIgnoreCase("OUT")) {
                if (device.getAvailableOutPorts() != null)
                    device.setAvailableOutPorts(device.getAvailableOutPorts() - 1);
                else
                    device.setAvailableOutPorts(0);
            }
            NetworkDeviceDTO otherDeviceDTO = networkDeviceMapper.domainToDTO(device, new CycleAvoidingMappingContext());
            try {
                saveEntity(otherDeviceDTO);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    /**
     * Save current network device.
     *
     * @param currentDeviceMapping the current device mapping
     */
    private void saveCurrentNetworkDevice(NetworkDeviceBind currentDeviceMapping) {
        Optional<NetworkDevices> currentDevice = networkDeviceRepository.findById(currentDeviceMapping.getCurrentDeviceId());
        currentDevice.ifPresent(device -> {
            int availablePorts = device.getAvailablePorts();
            int newAvailablePorts = Math.max(availablePorts - 1, 1);
            device.setAvailablePorts(newAvailablePorts);
            if (currentDeviceMapping.getPortType().equalsIgnoreCase("IN")) {
                if (device.getAvailableInPorts() != null)
                    device.setAvailableInPorts(device.getAvailableInPorts() - 1);
                else
                    device.setAvailableInPorts(0);
            }

            if (currentDeviceMapping.getPortType().equalsIgnoreCase("OUT")) {
                if (device.getAvailableOutPorts() != null)
                    device.setAvailableOutPorts(device.getAvailableOutPorts() - 1);
                else
                    device.setAvailableOutPorts(0);
            }

            NetworkDeviceDTO currentDeviceDTO = networkDeviceMapper.domainToDTO(device, new CycleAvoidingMappingContext());
            try {
                saveEntity(currentDeviceDTO);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    /**
     * Gets other device mapping.
     * @param dataStoreMappingDto the data store mapping dto
     * @param currentDeviceMapping the current device mapping
     * @param currentDeviceType the current device type
     * @param currentProductName the current product name
     * @param setCurrentPortNumber the set current port number
     * @param otherDeviceType the other device type
     * @param otherProductName the other product name
     * @param setOtherPortNumber the set other port number
     * @return the other device mapping
     */
    private NetworkDeviceBind getOtherDeviceMapping(NetworkDeviceBindDTO dataStoreMappingDto, NetworkDeviceBind currentDeviceMapping, String currentDeviceType, String currentProductName, String setCurrentPortNumber, String otherDeviceType, String otherProductName, String setOtherPortNumber) {
        NetworkDeviceBind otherDeviceMapping = networkDeviceBindMapper.dtoToDomain(dataStoreMappingDto, new CycleAvoidingMappingContext());
        otherDeviceMapping.setCurrentDeviceId(dataStoreMappingDto.getOtherDeviceId());
        otherDeviceMapping.setOtherDevicePort(dataStoreMappingDto.getCurrentDevicePort());
        otherDeviceMapping.setCurrentDevicePort(dataStoreMappingDto.getOtherDevicePort());
        otherDeviceMapping.setOtherDeviceId(dataStoreMappingDto.getCurrentDeviceId());
        otherDeviceMapping.setMappingId(currentDeviceMapping.getMappingId());
        if (otherDeviceMapping.getPortType().equalsIgnoreCase("in"))
            otherDeviceMapping.setPortType("out");
        else
            otherDeviceMapping.setPortType("in");
        otherDeviceMapping.setMappingId(dataStoreMappingDto.getMappingId());
        otherDeviceMapping.setCurrentDevice(otherProductName);
        otherDeviceMapping.setOtherDevice(currentProductName);
        otherDeviceMapping.setCurrentDevicePortNumber(setOtherPortNumber);
        otherDeviceMapping.setOtherDevicePortNumber(setCurrentPortNumber);
        otherDeviceMapping.setCurrentDeviceType(otherDeviceType);
        otherDeviceMapping.setOtherDeviceType(currentDeviceType);
        return otherDeviceMapping;
    }

    /**
     * Gets current device mapping.
     * @param dataStoreMappingDto the data store mapping dto
     * @param currentDeviceType the current device type
     * @param currentProductName the current product name
     * @param setCurrentPortNumber the set current port number
     * @param otherDeviceType the other device type
     * @param otherProductName the other product name
     * @param setOtherPortNumber the set other port number
     * @return the current device mapping
     */
    private NetworkDeviceBind getCurrentDeviceMapping(NetworkDeviceBindDTO dataStoreMappingDto, String currentDeviceType, String currentProductName, String setCurrentPortNumber, String otherDeviceType, String otherProductName, String setOtherPortNumber) {
        NetworkDeviceBind currentDeviceMapping = networkDeviceBindMapper.dtoToDomain(dataStoreMappingDto, new CycleAvoidingMappingContext());
        currentDeviceMapping.setCurrentDevicePort(dataStoreMappingDto.getCurrentDevicePort());
        currentDeviceMapping.setPortType(dataStoreMappingDto.getPortType());
        currentDeviceMapping.setCurrentDevice(currentProductName);
        currentDeviceMapping.setOtherDevice(otherProductName);
        currentDeviceMapping.setCurrentDevicePortNumber(setCurrentPortNumber);
        currentDeviceMapping.setOtherDevicePortNumber(setOtherPortNumber);
        currentDeviceMapping.setCurrentDeviceType(currentDeviceType);
        currentDeviceMapping.setOtherDeviceType(otherDeviceType);
        return currentDeviceMapping;
    }

    /**
     * Convert mapping to dtos network device bind dto.
     *
     * @param networkDeviceBind the network device bind
     * @return the network device bind dto
     */
    public NetworkDeviceBindDTO convertMappingToDTOS(NetworkDeviceBind networkDeviceBind) {
        try {
            NetworkDeviceBindDTO networkDeviceBindDTO = new NetworkDeviceBindDTO();
            networkDeviceBindDTO.setId(networkDeviceBind.getId());
            networkDeviceBindDTO.setCurrentDeviceId(networkDeviceBind.getCurrentDeviceId());
            networkDeviceBindDTO.setPortType(networkDeviceBind.getPortType());
            networkDeviceBindDTO.setOtherDeviceId(networkDeviceBind.getOtherDeviceId());
            networkDeviceBindDTO.setMappingId(networkDeviceBind.getMappingId());
            networkDeviceBindDTO.setOtherDevicePort(networkDeviceBind.getOtherDevicePort());
            networkDeviceBindDTO.setCurrentDevicePort(networkDeviceBind.getCurrentDevicePort());
            networkDeviceBindDTO.setCurrentDevice(networkDeviceBind.getCurrentDevice());
            networkDeviceBindDTO.setOtherDevice(networkDeviceBind.getOtherDevice());
            networkDeviceBindDTO.setCurrentDevicePortNumber(networkDeviceBind.getCurrentDevicePortNumber());
            networkDeviceBindDTO.setOtherDevicePortNumber(networkDeviceBind.getOtherDevicePortNumber());
            networkDeviceBindDTO.setCurrentDeviceType(networkDeviceBind.getCurrentDeviceType());
            networkDeviceBindDTO.setOtherDeviceType(networkDeviceBind.getOtherDeviceType());
            return networkDeviceBindDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert mapping to dtoss network device bind dto.
     *
     * @param networkDeviceBind the network device bindings
     * @return the network device bind dto
     */
    public NetworkDeviceBindDTO convertMappingToDTOSS(NetworkDeviceBind networkDeviceBind) {
        try {
            NetworkDeviceBindDTO networkDeviceBindDTO = new NetworkDeviceBindDTO();
            networkDeviceBindDTO.setId(networkDeviceBind.getId());
            networkDeviceBindDTO.setDeviceName(networkDeviceRepository.findById(networkDeviceBind.getOtherDeviceId()).get().getName());
            networkDeviceBindDTO.setParentDeviceName(networkDeviceRepository.findById(networkDeviceBind.getCurrentDeviceId()).get().getName());
            networkDeviceBindDTO.setCurrentDeviceId(networkDeviceBind.getCurrentDeviceId());
            networkDeviceBindDTO.setPortType(networkDeviceBind.getPortType());
            networkDeviceBindDTO.setOtherDeviceId(networkDeviceBind.getOtherDeviceId());
            networkDeviceBindDTO.setMappingId(networkDeviceBind.getMappingId());
            networkDeviceBindDTO.setOtherDevicePort(networkDeviceBind.getCurrentDevicePort());
            networkDeviceBindDTO.setCurrentDevicePort(networkDeviceBind.getOtherDevicePort());
            networkDeviceBindDTO.setCurrentDevice(networkDeviceBind.getCurrentDevice());
            networkDeviceBindDTO.setOtherDevice(networkDeviceBind.getOtherDevice());
            networkDeviceBindDTO.setCurrentDevicePortNumber(networkDeviceBind.getCurrentDevicePortNumber());
            networkDeviceBindDTO.setOtherDevicePortNumber(networkDeviceBind.getOtherDevicePortNumber());
            networkDeviceBindDTO.setCurrentDeviceType(networkDeviceBind.getCurrentDeviceType());
            networkDeviceBindDTO.setOtherDeviceType(networkDeviceBind.getOtherDeviceType());
            return networkDeviceBindDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all mapping data.
     *
     * @param id the id
     * @return the all mapping data
     */
    public List<NetworkDeviceBindDTO> getAllMappingData(Long id) {
        try {
            List<NetworkDeviceBindDTO> networkDeviceBindingsList = new ArrayList<>();
            networkDeviceBindingsList.addAll(networkdeviceBindRepository.findByCurrentDeviceId(id).stream().map(this::convertMappingToDTOS).collect(Collectors.toList()));
            List<NetworkDeviceBindDTO> parents = networkdeviceBindRepository.findByCurrentDeviceId(id).stream().map(this::convertMappingToDTOS).collect(Collectors.toList());
            List<NetworkDeviceBindDTO> reversedParents = new ArrayList<>();
            for (NetworkDeviceBindDTO networkDeviceBindDTO : parents) {
                NetworkDeviceBindDTO reversedParent = new NetworkDeviceBindDTO();
                reversedParent.setId(networkDeviceBindDTO.getId());
                reversedParent.setPortType(networkDeviceBindDTO.getPortType().equalsIgnoreCase(CommonConstants.IN) ? CommonConstants.OUT : CommonConstants.IN);
                reversedParent.setCurrentDevicePort(networkDeviceBindDTO.getCurrentDevicePort());
                reversedParent.setCurrentDeviceId(networkDeviceBindDTO.getCurrentDeviceId());
                reversedParent.setOtherDeviceId(networkDeviceBindDTO.getOtherDeviceId());
                reversedParent.setOtherDevicePort(networkDeviceBindDTO.getOtherDevicePort());
                reversedParent.setMappingId(networkDeviceBindDTO.getMappingId());
                reversedParent.setCurrentDevice(networkDeviceBindDTO.getCurrentDevice());
                reversedParent.setOtherDevice(networkDeviceBindDTO.getOtherDevice());
                reversedParent.setCurrentDevicePortNumber(networkDeviceBindDTO.getCurrentDevicePortNumber());
                reversedParent.setOtherDevicePortNumber(networkDeviceBindDTO.getOtherDevicePortNumber());
                reversedParent.setCurrentDeviceType(networkDeviceBindDTO.getCurrentDeviceType());
                reversedParent.setOtherDeviceType(networkDeviceBindDTO.getOtherDeviceType());
                reversedParents.add(reversedParent);
            }
            networkDeviceBindingsList.addAll(reversedParents);
//        networkDeviceBindingsList.addAll(networkDeviceBindingsRepository.findByParentDeviceId(id).stream(c).map(this::convertMappingToDTO).collect(Collectors.toList()));
            return networkDeviceBindingsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets network device by inventory mapping id.
     *
     * @param id the id
     * @return the network device by inventory mapping id
     */
    public NetworkDevices getNetworkDeviceByInventoryMappingId(Long id) {
        NetworkDevices networkDevice = null;
        if (id != null)
            networkDevice = networkDeviceRepository.findByInventorymappingId(id);
        return networkDevice;
    }


    /**
     * Replace network device binding with new device boolean.
     *
     * @param oldMacMappingId the old mac mapping id
     * @param inventoryMappingId the inventory mapping id
     * @return the boolean
     */
    @Transactional
    public Boolean replaceNetworkDeviceBindingWithNewDevice(Long oldMacMappingId, Long inventoryMappingId) {
        try {
            Optional<InOutWardMACMapping> oldInOutWardMACMapping = inOutWardMacRepo.findById(oldMacMappingId);
            if (oldInOutWardMACMapping.isPresent()) {
                if (oldInOutWardMACMapping.get().getInventoryMappingId() != null && inventoryMappingId != null) {
                    NetworkDevices oldDevice = networkDeviceRepository.findByInventorymappingId(oldInOutWardMACMapping.get().getInventoryMappingId());
                    NetworkDevices newDevice = networkDeviceRepository.findByInventorymappingId(inventoryMappingId);
                    //Set<String> availablePort=getPortsAvailability(newDevice.getId());
                    //List<String> inPorts=availablePort.stream().filter(x->x.contains("-IN-Port-")).collect(Collectors.toList());
                    //List<String> outPorts=availablePort.stream().filter(x->x.contains("-OUT-Port-")).collect(Collectors.toList())

                    if (oldDevice != null) {
                        List<NetworkDeviceBind> deviceBinds = networkdeviceBindRepository.findByCurrentDeviceId(oldDevice.getId());
                        List<NetworkDeviceBind> inDeviceBind = deviceBinds.stream().filter(x -> x.getPortType().equalsIgnoreCase("IN")).collect(Collectors.toList());
                        List<NetworkDeviceBind> outDeviceBind = deviceBinds.stream().filter(x -> x.getPortType().equalsIgnoreCase("OUT")).collect(Collectors.toList());

                        inDeviceBind.stream().forEach(bind -> {
                            List<NetworkDeviceBind> list = networkdeviceBindRepository.findByMappingId(bind.getMappingId());
                            list.stream().forEach(record -> {
                                if (record.getCurrentDeviceId().equals(bind.getCurrentDeviceId())) {
                                    record.setCurrentDeviceId(newDevice.getId());
                                    String newDevicePort = newDevice.getName() + record.getCurrentDevicePort().substring(record.getCurrentDevicePort().indexOf("-IN-Port-"));
                                    record.setCurrentDevicePort(newDevicePort);
                                    String currentDeviceType = networkDeviceRepository.findDeviceTypeById(newDevice.getId());
                                    Long currentProductId = networkDeviceRepository.findProductIdById(newDevice.getId());
                                    String currentProductName = productRepository.findProductNameByProductId(currentProductId);
                                    // For current device
                                    String[] currentParts = record.getCurrentDevicePort().split("Port", 2);
                                    String setCurrentPortNumber = "Port" + currentParts[1];
                                    record.setCurrentDevicePortNumber(setCurrentPortNumber);
                                    record.setCurrentDevice(currentProductName);
                                    record.setCurrentDeviceType(currentDeviceType);
                                    networkdeviceBindRepository.save(record);
                                }

                                if (record.getCurrentDeviceId().equals(bind.getOtherDeviceId())) {
                                    record.setOtherDeviceId(newDevice.getId());
                                    String newDevicePort = newDevice.getName() + record.getOtherDevicePort().substring(record.getOtherDevicePort().indexOf("-IN-Port-"));
                                    record.setOtherDevicePort(newDevicePort);
                                    String otherDeviceType = networkDeviceRepository.findDeviceTypeById(newDevice.getId());
                                    Long otherProductId = networkDeviceRepository.findProductIdById(newDevice.getId());
                                    String otherProductName = productRepository.findProductNameByProductId(otherProductId);
                                    // For other device
                                    String[] otherParts = record.getOtherDevicePort().split("Port", 2);
                                    String setOtherPortNumber = "Port" + otherParts[1];
                                    record.setOtherDevicePortNumber(setOtherPortNumber);
                                    record.setOtherDevice(otherProductName);
                                    record.setOtherDeviceType(otherDeviceType);
                                    networkdeviceBindRepository.save(record);
                                }
                            });
                        });

                        outDeviceBind.stream().forEach(bind -> {
                            List<NetworkDeviceBind> list = networkdeviceBindRepository.findByMappingId(bind.getMappingId());
                            list.stream().forEach(record -> {
                                if (record.getCurrentDeviceId().equals(bind.getCurrentDeviceId())) {
                                    record.setCurrentDeviceId(newDevice.getId());
                                    String newDevicePort = newDevice.getName() + record.getCurrentDevicePort().substring(record.getCurrentDevicePort().indexOf("-OUT-Port-"));
                                    record.setCurrentDevicePort(newDevicePort);
                                    String currentDeviceType = networkDeviceRepository.findDeviceTypeById(newDevice.getId());
                                    Long currentProductId = networkDeviceRepository.findProductIdById(newDevice.getId());
                                    String currentProductName = productRepository.findProductNameByProductId(currentProductId);
                                    // For current device
                                    String[] currentParts = record.getCurrentDevicePort().split("Port", 2);
                                    String setCurrentPortNumber = "Port" + currentParts[1];
                                    record.setCurrentDevicePortNumber(setCurrentPortNumber);
                                    record.setCurrentDevice(currentProductName);
                                    record.setCurrentDeviceType(currentDeviceType);
                                    networkdeviceBindRepository.save(record);
                                }

                                if (record.getCurrentDeviceId().equals(bind.getOtherDeviceId())) {
                                    record.setOtherDeviceId(newDevice.getId());
                                    String newDevicePort = newDevice.getName() + record.getOtherDevicePort().substring(record.getOtherDevicePort().indexOf("-OUT-Port-"));
                                    record.setOtherDevicePort(newDevicePort);
                                    String otherDeviceType = networkDeviceRepository.findDeviceTypeById(newDevice.getId());
                                    Long otherProductId = networkDeviceRepository.findProductIdById(newDevice.getId());
                                    String otherProductName = productRepository.findProductNameByProductId(otherProductId);
                                    // For other device
                                    String[] otherParts = record.getOtherDevicePort().split("Port", 2);
                                    String setOtherPortNumber = "Port" + otherParts[1];
                                    record.setOtherDevicePortNumber(setOtherPortNumber);
                                    record.setOtherDevice(otherProductName);
                                    record.setOtherDeviceType(otherDeviceType);
                                    networkdeviceBindRepository.save(record);
                                }
                            });
                        });
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets device hierarchy mapping by id.
     *
     * @param id the id
     * @return the device hierarchy mapping by id
     */
    public List<NetworkDeviceHierarchyDTO> getDeviceHierarchyMappingById(Long id) {
        try {
            if (id == null) return Collections.emptyList();
            // Find root device by traversing up through parent bindings
            List<NetworkDeviceBind> parentDeviceBindings = networkdeviceBindRepository.findBYCurrentDeviceIdAndInPortType(id);
            while (!parentDeviceBindings.isEmpty()) {
                Long nextDeviceId = parentDeviceBindings.get(0).getOtherDeviceId();
                List<NetworkDeviceBind> nextBindings = networkdeviceBindRepository.findBYCurrentDeviceIdAndInPortType(nextDeviceId);
                if (nextBindings.isEmpty()) break;
                parentDeviceBindings = nextBindings;
            }
            if (!parentDeviceBindings.isEmpty()) {
                Long rootDeviceId = parentDeviceBindings.get(0).getOtherDeviceId();
                return getDeviceHierarchyList(rootDeviceId);
            } else {
                return getDeviceHierarchyList(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting device hierarchy: " + e.getMessage());
        }
    }


    /**
     * Gets device hierarchy list.
     *
     * @param parentDeviceId the parent device id
     * @return the device hierarchy list
     */
    private List<NetworkDeviceHierarchyDTO> getDeviceHierarchyList(Long parentDeviceId) {
        List<NetworkDeviceHierarchyDTO> hierarchyList = new ArrayList<>();
        if (parentDeviceId == null) return hierarchyList;
        List<NetworkDeviceBind> childBinds = networkdeviceBindRepository.findByCurrentDeviceIdAndOutPortType(parentDeviceId);
        for (NetworkDeviceBind bind : childBinds) {
            NetworkDeviceHierarchyDTO dto = new NetworkDeviceHierarchyDTO();
            dto.setParentDeviceId(bind.getCurrentDeviceId());
            dto.setChildDeviceId(bind.getOtherDeviceId());
            dto.setParentDeviceName(bind.getCurrentDevice());
            dto.setChildDeviceName(bind.getOtherDevice());
            dto.setParentDevicePortNumber(bind.getCurrentDevicePortNumber());
            dto.setChildDevicePortNumber(bind.getOtherDevicePortNumber());
            dto.setParentDeviceType(bind.getCurrentDeviceType());
            dto.setChildDeviceType(bind.getOtherDeviceType());
            dto.setParentDevicePortType("IN");
            dto.setChildDevicePortType("OUT");
            // Get parent & child device details
            NetworkDevices parentDevice = networkDeviceRepository.findDetailsById(bind.getCurrentDeviceId());
            NetworkDevices childDevice = networkDeviceRepository.findDetailsById(bind.getOtherDeviceId());
            // Set parent owner type
            if (parentDevice != null) {
                if (parentDevice.getCustInventoryId() != null) {
                    Long customerId = customerInventoryMappingRepo.findCustomerIdByMappingId(parentDevice.getCustInventoryId());
                    String customerName = customersRepository.findCustomerUserNameBYId(customerId.intValue());
                    dto.setParentDeviceOwnerType("Customer");
                    dto.setParentOwnerName(customerName);
                } else if (parentDevice.getInventorymappingId() != null) {
                    String ownerType = inventoryMappingRepo.findOwnerTypeById(parentDevice.getInventorymappingId());
                    Long ownerId = inventoryMappingRepo.findOwnerIdById(parentDevice.getInventorymappingId());
                    if (ownerType.equalsIgnoreCase("pop")) {
                        Optional<String> popName = popManagementRepository.findNameById(ownerId);
                        popName.ifPresent(pop -> {
                            dto.setParentDeviceOwnerType("Pop");
                            dto.setParentOwnerName(pop);
                        });
                    } else if (ownerType.equalsIgnoreCase("Service Area")) {
                        String serviceAreaName = serviceAreaRepository.findServiceAreaNameById(ownerId);
                        dto.setParentDeviceOwnerType("Service Area");
                        dto.setParentOwnerName(serviceAreaName);
                    } else {
                        dto.setParentDeviceOwnerType(ownerType);
                        dto.setParentOwnerName("");
                    }
                }
            }
            // Set child owner type (was incorrectly using parent before)
            if (childDevice != null) {
                if (childDevice.getCustInventoryId() != null) {
                    Long customerId = customerInventoryMappingRepo.findCustomerIdByMappingId(childDevice.getCustInventoryId());
                    String customerName = customersRepository.findCustomerUserNameBYId(customerId.intValue());
                    dto.setChildDeviceOwnerType("Customer");
                    dto.setChildOwnerName(customerName);
                } else if (childDevice.getInventorymappingId() != null) {
                    String ownerType = inventoryMappingRepo.findOwnerTypeById(childDevice.getInventorymappingId());
                    Long ownerId = inventoryMappingRepo.findOwnerIdById(childDevice.getInventorymappingId());
                    if (ownerType.equalsIgnoreCase("pop")) {
                        Optional<String> popName = popManagementRepository.findNameById(ownerId);
                        popName.ifPresent(pop -> {
                            dto.setChildDeviceOwnerType("Pop");
                            dto.setChildOwnerName(pop);
                        });
                    } else if (ownerType.equalsIgnoreCase("Service Area")) {
                        String serviceAreaName = serviceAreaRepository.findServiceAreaNameById(ownerId);
                        dto.setChildDeviceOwnerType("Service Area");
                        dto.setChildOwnerName(serviceAreaName);
                    } else {
                        dto.setChildDeviceOwnerType(ownerType);
                        dto.setChildOwnerName("");
                    }
                }
            }
            // Set parent item details
            if (parentDevice != null) {
                Item parentItem = itemRepository.findItemDetailsById(parentDevice.getItemId());
                if (parentItem != null) {
                    dto.setParentDeviceMacAddress(parentItem.getMacAddress());
                    dto.setParentDeviceSerialNumber(parentItem.getSerialNumber());
                }
            }
            // Set child item details
            if (childDevice != null) {
                Item childItem = itemRepository.findItemDetailsById(childDevice.getItemId());
                if (childItem != null) {
                    dto.setChildDeviceMacAddress(childItem.getMacAddress());
                    dto.setChildDeviceSerialNumber(childItem.getSerialNumber());
                }
            }
            hierarchyList.add(dto);
            // Recursively fetch children of this child
            List<NetworkDeviceHierarchyDTO> childHierarchy = getDeviceHierarchyList(bind.getOtherDeviceId());
            hierarchyList.addAll(childHierarchy);
        }
        return hierarchyList;
    }

    /**
     * Gets olt device by pop id.
     *
     * @param popId the pop id
     * @return the olt device by pop id
     */
    public List<NetworkDeviceDTO> getOLTDeviceByPopId(Long popId) {
        String SUB_MODULE = getModuleNameForLog() + "[getOLTDeviceByPopId()]";
        if (popId == null) {
            logger.error("Module: {} - Please select POP", SUB_MODULE);
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select pop", null);
        }
        try {
            List<Long> inventoryMappingIds;
            List<NetworkDevices> networkDevices = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                inventoryMappingIds = inventoryMappingRepo.findIdsByOwnerIdAndPOPType(popId);
                if (!inventoryMappingIds.isEmpty()) {
                    networkDevices = networkDeviceRepository.findActiveOltDeviceIdsByInventoryMapping(inventoryMappingIds);
                }
            } else {
                List<Integer> mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
                inventoryMappingIds = inventoryMappingRepo.findIdsByOwnerIdAndPOPTypeAndMvnoId(popId, mvnoIds);
                if (!inventoryMappingIds.isEmpty()) {
                    networkDevices = networkDeviceRepository.findActiveOltDeviceIdsByMvnoAndInventoryMapping(mvnoIds, inventoryMappingIds);
                }
            }
            return mapDevicesToDTOWithProductName(networkDevices);
        } catch (Exception e) {
            logger.error("Module: {} - Exception occurred while fetching OLT devices: {}", SUB_MODULE, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Map devices to dto with product name list.
     *
     * @param networkDevices the network devices
     * @return the list
     */
    private List<NetworkDeviceDTO> mapDevicesToDTOWithProductName(List<NetworkDevices> networkDevices) {
        if (networkDevices == null || networkDevices.isEmpty()) {
            return Collections.emptyList();
        }
        return networkDevices.stream()
                .map(device -> {
                    NetworkDeviceDTO dto = networkDeviceMapper.domainToDTO(device, new CycleAvoidingMappingContext());
                    Long productId = networkDeviceRepository.findProductIdById(dto.getId());
                    if (productId != null) {
                        dto.setProductId(productId);
                        dto.setProductName(productRepository.findProductNameByProductId(productId));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets dn splitter by olt id.
     *
     * @param oltId the olt id
     * @return the dn splitter by olt id
     */
    public List<NetworkDeviceDTO> getDNSplitterByOltId(Long oltId) {
        String SUB_MODULE = getModuleNameForLog() + "[getDNSplitterByOltId()]";
        if (oltId == null) {
            logger.error("Module: {} - Please select OLT", SUB_MODULE);
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select OLT", null);
        }
        try {
            List<Long> dnSplitterByOLTId = networkdeviceBindRepository.findDnSplitterByOLTId(oltId);
            List<NetworkDevices> networkDevices = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                networkDevices = networkDeviceRepository.findByIdIn(dnSplitterByOLTId);
            } else {
                networkDevices = networkDeviceRepository.findByIdInAndMvnoIdIn(dnSplitterByOLTId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
            return mapDevicesToDTOWithProductName(networkDevices);
        } catch (Exception e) {
            logger.error("Module: {} - Exception occurred while fetching DN Splitter devices: {}", SUB_MODULE, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Gets sn splitter by dn splitter id.
     *
     * @param dnSplitterId the dn splitter id
     * @return the sn splitter by dn splitter id
     */
    public List<NetworkDeviceDTO> getSNSplitterByDNSplitterId(Long dnSplitterId) {
        String SUB_MODULE = getModuleNameForLog() + "[getSNSplitterByDNSplitterId()]";
        if (dnSplitterId == null) {
            logger.error("Module: {} - Please select DN Splitter", SUB_MODULE);
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select DN Splitter", null);
        }
        try {
            List<Long> snSplitterByOLTId = networkdeviceBindRepository.findSNSplitterByDNSpliterId(dnSplitterId);
            List<NetworkDevices> networkDevices = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                networkDevices = networkDeviceRepository.findByIdIn(snSplitterByOLTId);
            } else {
                networkDevices = networkDeviceRepository.findByIdInAndMvnoIdIn(snSplitterByOLTId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
            return mapDevicesToDTOWithProductName(networkDevices);
        } catch (Exception e) {
            logger.error("Module: {} - Exception occurred while fetching SN Splitter devices: {}", SUB_MODULE, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // Enhanced method to get complete network path hierarchy
    public List<DeviceHierarchy> getCompleteNetworkPathHierarchy(Long custId, Long targetDeviceId) {
        try {
            // Get customer network binding
            CustomerNetworkBind bind = customerNetworkBindRepository.findByCustomerId(custId)
                    .orElseThrow(() -> new RuntimeException("Customer network bind not found for customer ID: " + custId));

            // Find the complete path to the target device
            List<Long> completePath = findCompletePathToDevice(targetDeviceId);

            if (completePath.isEmpty()) {
                // Fallback to basic path if complete path not found
                return getBasicPathHierarchy(bind, targetDeviceId);
            }

            // Build hierarchy from the complete path
            return buildHierarchyFromCompletePath(completePath, targetDeviceId, bind);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Method to find complete path to target device by traversing backwards
    private List<Long> findCompletePathToDevice(Long targetDeviceId) {
        List<Long> path = new ArrayList<>();
        Long currentDeviceId = targetDeviceId;
        Set<Long> visited = new HashSet<>();

        // Add target device to path
        path.add(currentDeviceId);
        visited.add(currentDeviceId);

        // Traverse backwards to find the root
        while (currentDeviceId != null) {
            // Find incoming connection to current device
            NetworkDeviceBindDTO incomingConnection = networkdeviceBindRepository.findByOtherDeviceId(currentDeviceId)
                    .stream()
                    .map(this::convertMappingToDTOSS)
                    .filter(dto -> dto.getPortType().equalsIgnoreCase("OUT"))
                    .filter(dto -> !visited.contains(dto.getCurrentDeviceId()))
                    .findFirst()
                    .orElse(null);

            if (incomingConnection != null) {
                currentDeviceId = incomingConnection.getCurrentDeviceId();
                path.add(0, currentDeviceId); // Add to beginning of path
                visited.add(currentDeviceId);
            } else {
                break; // No more parent devices found
            }
        }

        return path;
    }

    // Method to build hierarchy from complete path
    private List<DeviceHierarchy> buildHierarchyFromCompletePath(List<Long> devicePath, Long selectedDeviceId, CustomerNetworkBind bind) {
        if (devicePath.isEmpty()) {
            return new ArrayList<>();
        }

        // Build nested hierarchy
        DeviceHierarchy rootNode = null;
        DeviceHierarchy currentNode = null;

        for (int i = 0; i < devicePath.size(); i++) {
            Long deviceId = devicePath.get(i);
            if (deviceId == null) continue;
            NetworkDevices device = networkDeviceRepository.findById(deviceId).orElse(null);

            DeviceHierarchy deviceNode = createEnhancedDeviceNode(device, deviceId, selectedDeviceId, bind);

            if (rootNode == null) {
                rootNode = deviceNode;
                currentNode = deviceNode;
            } else {
                currentNode.setChildren(Arrays.asList(deviceNode));
                currentNode = deviceNode;
            }
        }

        return rootNode != null ? Arrays.asList(rootNode) : new ArrayList<>();
    }

    // Enhanced device node creation with support for more device types
    private DeviceHierarchy createEnhancedDeviceNode(NetworkDevices device, Long deviceId, Long selectedDeviceId, CustomerNetworkBind bind) {
        String styleClass = "";
        String img = "";
        String label = "";
        String deviceName = device.getName();

        // Determine device type and styling
        String deviceType = device.getDevicetype().toLowerCase();

        switch (deviceType) {
            case "master db/db":
            case "master db":
                styleClass = getStyle(deviceId, selectedDeviceId, "bg-orange-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png";
                label = "Master DB";
                break;

            case "switch":
                styleClass = getStyle(deviceId, selectedDeviceId, "bg-grey-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png";
                label = "Switch";
                break;

            case "olt":
                styleClass = getStyle(deviceId, selectedDeviceId, "bg-indigo-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/02_OLT_Y2.png";
                label = "OLT";
                break;

            case "splitter":
                // Determine if DN or SN splitter based on CustomerNetworkBind
                if (deviceId.equals(bind.getDnSplitterId())) {
                    label = "DN Splitter";
                } else if (deviceId.equals(bind.getSnSplitterId())) {
                    label = "SN Splitter";
                } else {
                    label = "Splitter";
                }
                styleClass = getStyle(deviceId, selectedDeviceId, "bg-purple-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png";
                break;

            case "onu":
                styleClass = getStyle(deviceId, selectedDeviceId, "bg-teal-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/01_ONU_Y2.png";
                label = "ONU";
                break;

            case "router":
                styleClass = getStyle(deviceId, selectedDeviceId, "bg-green-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png";
                label = "Router";
                break;

            default:
                // Handle any other device types
                styleClass = getStyle(deviceId, selectedDeviceId, "bg-indigo-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/02_OLT_Y2.png";
                label = device.getDevicetype().isEmpty() ? "ONT" : device.getDevicetype();
                break;
        }

        return new DeviceHierarchy(
                "Network",
                true,
                styleClass,
                new GraphData(
                        img,
                        deviceName,
                        label,
                        deviceName
                ),
                new ArrayList<>() // Children will be set separately
        );
    }

    // Fallback method for basic path (original version 5 logic)
    private List<DeviceHierarchy> getBasicPathHierarchy(CustomerNetworkBind bind, Long targetDeviceId) {
        try {
            // Get devices
            NetworkDevices oltDevice = networkDeviceRepository.findById(bind.getOltId()).orElse(null);
            NetworkDevices dnSplitter = networkDeviceRepository.findById(bind.getDnSplitterId()).orElse(null);
            NetworkDevices snSplitter = networkDeviceRepository.findById(bind.getSnSplitterId()).orElse(null);
            NetworkDevices targetDevice = networkDeviceRepository.findById(targetDeviceId).orElse(null);

            if (oltDevice == null || targetDevice == null) {
                return new ArrayList<>();
            }

            // Build basic hierarchy (OLT -> DN -> SN -> Target)
            DeviceHierarchy targetNode = createEnhancedDeviceNode(targetDevice, targetDeviceId, targetDeviceId, bind);

            DeviceHierarchy snNode = snSplitter != null ?
                    createEnhancedDeviceNode(snSplitter, bind.getSnSplitterId(), targetDeviceId, bind) : null;
            if (snNode != null) snNode.setChildren(Arrays.asList(targetNode));

            DeviceHierarchy dnNode = dnSplitter != null ?
                    createEnhancedDeviceNode(dnSplitter, bind.getDnSplitterId(), targetDeviceId, bind) : null;
            if (dnNode != null) dnNode.setChildren(snNode != null ? Arrays.asList(snNode) : Arrays.asList(targetNode));

            DeviceHierarchy oltNode = createEnhancedDeviceNode(oltDevice, bind.getOltId(), targetDeviceId, bind);
            oltNode.setChildren(dnNode != null ? Arrays.asList(dnNode) : Arrays.asList(targetNode));

            return Arrays.asList(oltNode);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Method to find path between two devices using BFS
    private List<Long> findPathBetweenDevices(Long startId, Long endId) {
        if (startId.equals(endId)) {
            return Arrays.asList(startId);
        }

        Queue<List<Long>> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        queue.offer(Arrays.asList(startId));
        visited.add(startId);

        while (!queue.isEmpty()) {
            List<Long> currentPath = queue.poll();
            Long currentDevice = currentPath.get(currentPath.size() - 1);

            // Get all outgoing connections from current device
            List<NetworkDeviceBindDTO> connections = networkdeviceBindRepository.findByCurrentDeviceId(currentDevice)
                    .stream()
                    .map(this::convertMappingToDTOSS)
                    .filter(dto -> dto.getPortType().equalsIgnoreCase("OUT"))
                    .collect(Collectors.toList());

            for (NetworkDeviceBindDTO connection : connections) {
                Long nextDeviceId = connection.getOtherDeviceId();

                if (nextDeviceId.equals(endId)) {
                    // Found the target device
                    List<Long> completePath = new ArrayList<>(currentPath);
                    completePath.add(nextDeviceId);
                    return completePath;
                }

                if (!visited.contains(nextDeviceId)) {
                    visited.add(nextDeviceId);
                    List<Long> newPath = new ArrayList<>(currentPath);
                    newPath.add(nextDeviceId);
                    queue.offer(newPath);
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    // Recursive method to build children following the specific path
    private List<DeviceHierarchy> buildChildrenFromPath(List<Long> devicePath, int currentIndex, Long selectedId, CustomerNetworkBind customerNetworkBind) {
        if (currentIndex >= devicePath.size() - 1) {
            return new ArrayList<>();
        }

        Long currentDeviceId = devicePath.get(currentIndex);
        Long nextDeviceId = devicePath.get(currentIndex + 1);

        NetworkDeviceBindDTO connection = networkdeviceBindRepository.findByCurrentDeviceId(currentDeviceId)
                .stream()
                .map(this::convertMappingToDTOSS)
                .filter(dto -> dto.getPortType().equalsIgnoreCase("OUT"))
                .filter(dto -> dto.getOtherDeviceId().equals(nextDeviceId))
                .findFirst()
                .orElse(null);

        if (connection == null) {
            return new ArrayList<>();
        }

        NetworkDevices nextDevice = networkDeviceRepository.findById(nextDeviceId).orElse(null);
        if (nextDevice == null) {
            return new ArrayList<>();
        }

        DeviceHierarchy childNode = createDeviceNode(nextDevice, nextDeviceId, selectedId, customerNetworkBind, connection);

        if (currentIndex + 1 < devicePath.size() - 1) {
            childNode.setChildren(buildChildrenFromPath(devicePath, currentIndex + 1, selectedId, customerNetworkBind));
        } else {
            childNode.setChildren(new ArrayList<>());
        }

        return Arrays.asList(childNode);
    }

    private DeviceHierarchy createDeviceNode(NetworkDevices device, Long deviceId, Long selectedId, CustomerNetworkBind customerNetworkBind, NetworkDeviceBindDTO connection) {
        String styleClass = "";
        String img = "";
        String label = "";
        String deviceName = device.getName(); // Use actual device name, not port name

        // Determine device type and styling
        switch (device.getDevicetype().toLowerCase()) {
            case "olt":
                styleClass = getStyle(deviceId, selectedId, "bg-indigo-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/02_OLT_Y2.png";
                label = "OLT";
                break;

            case "splitter":
                // Determine if DN or SN splitter based on CustomerNetworkBind
                if (deviceId.equals(customerNetworkBind.getDnSplitterId())) {
                    label = "DN Splitter";
                    styleClass = getStyle(deviceId, selectedId, "bg-purple-500");
                } else if (deviceId.equals(customerNetworkBind.getSnSplitterId())) {
                    label = "SN Splitter";
                    styleClass = getStyle(deviceId, selectedId, "bg-purple-500");
                } else {
                    // Default splitter handling
                    label = "Splitter";
                    styleClass = getStyle(deviceId, selectedId, "bg-purple-500");
                }
                img = "assets/img/All_Icons/11_Network_Management/Map/04_Fiber_Y2.png";
                break;

            case "onu":
                styleClass = getStyle(deviceId, selectedId, "bg-teal-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/01_ONU_Y2.png";
                label = "ONU";
                break;

            default:
                // Handle any other device types
                styleClass = getStyle(deviceId, selectedId, "bg-indigo-500");
                img = "assets/img/All_Icons/11_Network_Management/Map/02_OLT_Y2.png";
                label = device.getDevicetype().isEmpty() ? "ONT" : device.getDevicetype();
                break;
        }

        return new DeviceHierarchy(
                "Network",
                true,
                styleClass,
                new GraphData(
                        img,
                        deviceName,  // Use device name
                        label,
                        deviceName   // Use device name for label too
                ),
                new ArrayList<>() // Children will be set separately
        );
    }

    public List<DeviceHierarchy> getTargetDeviceHierarchy(Long itemId, Long custId, Long custInveId) {
        try {
            Long targetDeviceId = networkDeviceRepository.findDeviceIdBYItemIdAndCustInventoryId(itemId, custInveId);
            return getCompleteNetworkPathHierarchy(custId, targetDeviceId);
        } catch (Exception e) {
            throw e;
        }
    }

    private String getStyle(Long deviceId, Long selectedId, String baseClass) {
        if (deviceId != null && selectedId != null && deviceId.equals(selectedId)) {
            return baseClass + " text-white selected-node";
        }
        return baseClass + " text-white";
    }

    public List<NetworkDeviceHierarchyDTO> getTargetDeviceHierarchyList(Long itemId, Long custId, Long custInvenId) {
        Long targetDeviceId = networkDeviceRepository.findDeviceIdBYItemIdAndCustInventoryId(itemId, custInvenId);
        if(targetDeviceId == null) {
            return Collections.emptyList();
        }
        List<Long> completePath = findCompletePathToDevice(targetDeviceId);
        if (completePath.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<NetworkDeviceBind> networkDeviceBinds = networkdeviceBindRepository.findByOtherDeviceIdsAndOutPortType(completePath);
            Collections.reverse(networkDeviceBinds);
            return getHierarchyList(networkDeviceBinds);
        }
    }

    private List<NetworkDeviceHierarchyDTO> getHierarchyList(List<NetworkDeviceBind> networkDeviceBinds) {
        try {
            List<NetworkDeviceHierarchyDTO> hierarchyList = new ArrayList<>();
            for (NetworkDeviceBind bind : networkDeviceBinds) {
                NetworkDeviceHierarchyDTO dto = new NetworkDeviceHierarchyDTO();
                dto.setParentDeviceId(bind.getCurrentDeviceId());
                dto.setChildDeviceId(bind.getOtherDeviceId());
                dto.setParentDeviceName(bind.getCurrentDevice());
                dto.setChildDeviceName(bind.getOtherDevice());
                dto.setParentDevicePortNumber(bind.getCurrentDevicePortNumber());
                dto.setChildDevicePortNumber(bind.getOtherDevicePortNumber());
                dto.setParentDeviceType(bind.getCurrentDeviceType());
                dto.setChildDeviceType(bind.getOtherDeviceType());
                dto.setParentDevicePortType("OUT");
                dto.setChildDevicePortType("IN");
                // Get parent & child device details
                NetworkDevices parentDevice = networkDeviceRepository.findDetailsById(bind.getCurrentDeviceId());
                NetworkDevices childDevice = networkDeviceRepository.findDetailsById(bind.getOtherDeviceId());
                // Set parent owner type
                if (parentDevice != null) {
                    if (parentDevice.getCustInventoryId() != null) {
                        Long customerId = customerInventoryMappingRepo.findCustomerIdByMappingId(parentDevice.getCustInventoryId());
                        String customerName = customersRepository.findCustomerUserNameBYId(customerId.intValue());
                        dto.setParentDeviceOwnerType("Customer");
                        dto.setParentOwnerName(customerName);
                    } else if (parentDevice.getInventorymappingId() != null) {
                        String ownerType = inventoryMappingRepo.findOwnerTypeById(parentDevice.getInventorymappingId());
                        Long ownerId = inventoryMappingRepo.findOwnerIdById(parentDevice.getInventorymappingId());
                        if (ownerType.equalsIgnoreCase("pop")) {
                            Optional<String> popName = popManagementRepository.findNameById(ownerId);
                            popName.ifPresent(pop -> {
                                dto.setParentDeviceOwnerType("Pop");
                                dto.setParentOwnerName(pop);
                            });
                        } else if (ownerType.equalsIgnoreCase("Service Area")) {
                            String serviceAreaName = serviceAreaRepository.findServiceAreaNameById(ownerId);
                            dto.setParentDeviceOwnerType("Service Area");
                            dto.setParentOwnerName(serviceAreaName);
                        } else {
                            dto.setParentDeviceOwnerType(ownerType);
                            dto.setParentOwnerName("");
                        }
                    }
                }
                // Set child owner type (was incorrectly using parent before)
                if (childDevice != null) {
                    if (childDevice.getCustInventoryId() != null) {
                        Long customerId = customerInventoryMappingRepo.findCustomerIdByMappingId(childDevice.getCustInventoryId());
                        String customerName = customersRepository.findCustomerUserNameBYId(customerId.intValue());
                        dto.setChildDeviceOwnerType("Customer");
                        dto.setChildOwnerName(customerName);
                    } else if (childDevice.getInventorymappingId() != null) {
                        String ownerType = inventoryMappingRepo.findOwnerTypeById(childDevice.getInventorymappingId());
                        Long ownerId = inventoryMappingRepo.findOwnerIdById(childDevice.getInventorymappingId());
                        if (ownerType.equalsIgnoreCase("pop")) {
                            Optional<String> popName = popManagementRepository.findNameById(ownerId);
                            popName.ifPresent(pop -> {
                                dto.setChildDeviceOwnerType("Pop");
                                dto.setChildOwnerName(pop);
                            });
                        } else if (ownerType.equalsIgnoreCase("Service Area")) {
                            String serviceAreaName = serviceAreaRepository.findServiceAreaNameById(ownerId);
                            dto.setChildDeviceOwnerType("Service Area");
                            dto.setChildOwnerName(serviceAreaName);
                        } else {
                            dto.setChildDeviceOwnerType(ownerType);
                            dto.setChildOwnerName("");
                        }
                    }
                }
                // Set parent item details
                if (parentDevice != null) {
                    Item parentItem = itemRepository.findItemDetailsById(parentDevice.getItemId());
                    if (parentItem != null) {
                        dto.setParentDeviceMacAddress(parentItem.getMacAddress());
                        dto.setParentDeviceSerialNumber(parentItem.getSerialNumber());
                    }
                }
                // Set child item details
                if (childDevice != null) {
                    Item childItem = itemRepository.findItemDetailsById(childDevice.getItemId());
                    if (childItem != null) {
                        dto.setChildDeviceMacAddress(childItem.getMacAddress());
                        dto.setChildDeviceSerialNumber(childItem.getSerialNumber());
                    }
                }
                hierarchyList.add(dto);
            }
            return hierarchyList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NetworkDeviceDTO getDeviceDetailsByItemId(Long itemId, Long custInventoryId) {
        try {
            NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(itemId, custInventoryId);
            if (networkDevices != null) {
                NetworkDeviceDTO networkDeviceDTO = new NetworkDeviceDTO();
                networkDeviceDTO.setId(networkDevices.getId());
                networkDeviceDTO.setName(networkDevices.getName());
                networkDeviceDTO.setDisplayname(networkDevices.getDisplayname());
                networkDeviceDTO.setProductId(networkDevices.getProduct().getId());
                networkDeviceDTO.setDevicetype(networkDevices.getDevicetype());
                networkDeviceDTO.setStatus(networkDevices.getStatus());
                networkDeviceDTO.setLatitude(networkDevices.getLatitude());
                networkDeviceDTO.setLongitude(networkDevices.getLongitude());
                networkDeviceDTO.setIsDeleted(networkDevices.getIsDeleted());
                networkDeviceDTO.setMvnoId(networkDevices.getMvnoId());
                networkDeviceDTO.setItemId(networkDevices.getItemId());
                networkDeviceDTO.setCustInventoryId(networkDevices.getCustInventoryId());
                networkDeviceDTO.setInventorymappingId(networkDevices.getInventorymappingId());
                return networkDeviceDTO;
            } else {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Network device is not found by item id: " + itemId + " and customer inventory id: " + custInventoryId, null);
            }
        } catch (CustomValidationException e) {
            throw e;
        }
    }
}
