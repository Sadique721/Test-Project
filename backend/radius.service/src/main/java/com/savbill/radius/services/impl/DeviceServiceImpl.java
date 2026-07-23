package com.savbill.radius.services.impl;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.aaa.data.CustomerDetails;
import com.savbill.radius.aaa.data.redis.CacheServiceWithRedis;
import com.savbill.radius.aaa.db.DBAccountingDriver;
import com.savbill.radius.aaa.db.DBAuthenticationDriver;
import com.savbill.radius.aaa.packet.AccessRequest;
import com.savbill.radius.aaa.packet.AccountingRequest;
import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.server.RadiusAsyncUtility;
import com.savbill.radius.aaa.server.RadiusUtility;
import com.savbill.radius.aaa.util.ValidateExpression;
import com.savbill.radius.config.CacheRetrival;
import com.savbill.radius.dto.LiveUserSearchDTO;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.DeviceDto;
import com.savbill.radius.helper.DeviceType;
import com.savbill.radius.helper.changeUserData;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.*;
import com.savbill.radius.utils.*;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.*;
import com.savbill.radius.utils.*;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CoaDMProfileRepository coaDMProfileRepository;

    @Autowired
    private CoaDMProfileServiceImpl coaDMProfileService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LiveUserService liveUserService;

    @Autowired
    private LiveUserService liverUserService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CoaDMProfileAttributeService coaDMProfileAttributeService;

    @Autowired
    private RadiusProfileRepository radiusProfileRepository;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);
    @Autowired
    private MacAddressMappingRepository macAddressMappingRepository;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private ClientGroupRepository clientGroupRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LiveUserRepository liveUserRepository;

    @Autowired
    private ClientGroupService clientGroupService;

    @Override
    public List<Device> findAll(Integer mvnoId) {
        try {
            QDevice qDevice = QDevice.device;
            BooleanExpression exp = qDevice.isNotNull();
            if (mvnoId != null && mvnoId == 1)
                return deviceRepository.findAll();
            else {
                exp = exp.and(qDevice.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                return (List<Device>) deviceRepository.findAll(exp);
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Device findById(Long id, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid device id.");
            } else {
                QDevice qDevice = QDevice.device;
                BooleanExpression boolExp = qDevice.isNotNull();
                boolExp = boolExp.and(qDevice.deviceId.eq(id));
                if (mvnoId == null || mvnoId != 1)
                    boolExp = boolExp.and(qDevice.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1).and(qDevice.deviceId.eq(id)));
                Optional<Device> deviceOptional = deviceRepository.findOne(boolExp);

                if (deviceOptional.isPresent()) {
                    return deviceOptional.get();
                } else {
                    throw new IllegalArgumentException(
                            "No record found with device id : '" + id + "'. Please enter valid device id");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Device validateDeviceForUpdateAndDelete(Long id, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid device id.");
            } else {
                QDevice qDevice = QDevice.device;
                BooleanExpression boolExp = qDevice.isNotNull();
                boolExp = boolExp.and(qDevice.deviceId.eq(id));
                if (mvnoId == null || mvnoId != 1)
                    boolExp = boolExp.and(qDevice.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)).and(qDevice.deviceId.eq(id)));
                Optional<Device> deviceOptional = deviceRepository.findOne(boolExp);

                if (deviceOptional.isPresent()) {
                    return deviceOptional.get();
                } else {
                    throw new IllegalArgumentException(
                            "No record found with device id : '" + id + "'. Please enter valid device id");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Device> findByName(String name, Integer mvnoId) {
        try {
            QDevice qDevice = QDevice.device;
            BooleanExpression boolExp = qDevice.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qDevice.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null")) {
                return (List<Device>) deviceRepository.findAll(boolExp);
            } else {
                boolExp = boolExp.and(qDevice.deviceProfileName.containsIgnoreCase(name));
                return (List<Device>) deviceRepository.findAll(boolExp);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Device add(DeviceDto deviceDto, Integer mvnoId) {
        try {

            Device addDevice = new Device();
            Device device = validateDeviceData(deviceDto, mvnoId, false);
            device.setCreatedBy(CommonConstants.USER_ADMIN);
            device.setCreatedOn(LocalDateTime.now());
            addDevice = deviceRepository.save(device);
            if (!deviceDto.getClientIds().isEmpty() && deviceDto.getClientIds() != null) {
                updateRadiusClient(deviceDto.getClientIds(), addDevice.getDeviceId());
            }
            return addDevice;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void updateRadiusClient(List<Long> clientIdList, Long deviceId) {
        for (Long clientId : clientIdList) {
            Client client = clientRepository.findByClientId(clientId);
            client.setDeviceId(deviceId);
            clientRepository.save(client);
        }
    }

    @Override
    public Device update(DeviceDto deviceDto, Integer mvnoId, HttpServletRequest request) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        try {
            List<Client> clientList = clientRepository.findByClientIdIn(deviceDto.getClientIds());
            Device updateDevice = new Device();
            Optional<Device> optional = deviceRepository.findByDeviceProfileNameAndMvnoId(deviceDto.getDeviceProfileName(), mvnoId);
            Device device = validateDeviceData(deviceDto, mvnoId, true);
            String updated = RadiusUtils.getUpdatedDiff(optional.get(), device);
            device.setLastModifiedBy(CommonConstants.USER_ADMIN);
            device.setLastModifiedOn(LocalDateTime.now());
            device.setClientList(clientList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices has been Upadted Successfully with updated data," + updated + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            updateDevice = deviceRepository.save(device);
            return updateDevice;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public void delete(String name, Integer mvnoId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_STRING_MSG + "Please enter valid device profile name");
            } else {
                QDevice qDevice = QDevice.device;
                BooleanExpression boolExp = qDevice.isNotNull();
                if (mvnoId == null || mvnoId != 1)
                    boolExp = boolExp.and(qDevice.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)).and(qDevice.deviceProfileName.eq(name)));
                boolExp = boolExp.and(qDevice.deviceProfileName.eq(name));
                Optional<Device> deviceOptional = deviceRepository.findOne(boolExp);
                if (deviceOptional.isPresent()) {
                    deviceRepository.delete(deviceOptional.get());
                    log.info("Device deleted succefully: " + name);
                } else {
                    throw new IllegalArgumentException("You do not have access to update or delete this record.");
                }
            }
        } catch (Throwable e) {
            //   log.error("Error while deleting device: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    private Long validateDeviceProfileType(Device device, String coaProfileName, Integer mvnoId) {
        try {
            Long coaDmProfileId = null;
            if (device.getType() == null) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid device profile type");
            } else if (!device.getType().equals(DeviceType.HTTP.toString()) && !device.getType().equals(DeviceType.COA.toString()) && !device.getType().equals(DeviceType.SNMP.toString())) {
                throw new IllegalArgumentException("Please enter valid type, Type should be '" + DeviceType.HTTP.toString() + "' OR '" + DeviceType.COA.toString() + "' OR '" + DeviceType.SNMP.toString() + "'");
            } else if (device.getType().equals(DeviceType.COA.toString())) {
                if (!ValidateCrudTransactionData.validateStringTypeFieldValue(coaProfileName))
                    throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid CoA/DM profile name.");
                QCoaDMProfile qCoaDmProfile = QCoaDMProfile.coaDMProfile;
                BooleanExpression boolExp = qCoaDmProfile.isNotNull();
                if (mvnoId == null || mvnoId != 1)
                    boolExp = boolExp.and(qCoaDmProfile.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
                boolExp = boolExp.and(qCoaDmProfile.name.eq(coaProfileName));

                Optional<CoaDMProfile> coaDMProfile = coaDMProfileRepository.findOne(boolExp);
                if (!coaDMProfile.isPresent()) {
                    throw new RuntimeException("No record found for COA Profile with the given profile name :'" + coaProfileName + "',Please enter valid COA Profile name.");
                } else {
                    coaDmProfileId = coaDMProfile.get().getCoaDMProfileId();
                }
                device.setLoginurl(null);
                device.setLogouturl(null);
            } else if (device.getType().equals(DeviceType.HTTP.toString())) {
                if (!ValidateCrudTransactionData.validateStringTypeFieldValue(device.getLoginurl())) {
                    throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid login url");
                } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(device.getLogouturl())) {
                    throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid logout url");
                }
                device.setCoaDmProfileId(null);
            }
            return coaDmProfileId;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Device validateDeviceData(DeviceDto deviceDto, Integer mvnoId, boolean isUpdate) {
        try {
            Device device = new Device(deviceDto);
            if (mvnoId == null || mvnoId != 1)
                device.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            else
                device.setMvnoId(mvnoId);
            validateDeviceProfileInfo(isUpdate, device);
            Long coaDmProfileId = validateDeviceProfileType(device, deviceDto.getCoaProfileName(), mvnoId);
            if (coaDmProfileId != null) {
                device.setCoaDmProfileId(coaDmProfileId);
            }
            if (device.getDescription() != null && device.getDescription().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
                device.setDescription(null);
            }
            if (device.getCheckItem() != null && device.getCheckItem().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
                device.setCheckItem(null);
            }
            return device;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Device validateDeviceProfileInfo(boolean isUpdate, Device device) {
        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(device.getDeviceProfileName())) {
            throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid device profile name");
        } else if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(device.getPriority())) {
            throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid priority value");
        } else if (device.getStatus() == null) {
            throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Please enter valid status value");
        } else if (isUpdate) {
            QDevice qDevice = QDevice.device;
            BooleanExpression boolExp = qDevice.isNotNull();
            if (device.getMvnoId() != null && device.getMvnoId() == 1)
                boolExp = boolExp.and(qDevice.deviceProfileName.eq(device.getDeviceProfileName()));
            else
                boolExp = boolExp.and(qDevice.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(device.getMvnoId())).and(qDevice.deviceProfileName.eq(device.getDeviceProfileName())));
            Optional<Device> deviceOptional = deviceRepository.findOne(boolExp);

            if (deviceOptional.isPresent()) {
                device.setCreatedOn(deviceOptional.get().getCreatedOn());
                device.setDeviceId(deviceOptional.get().getDeviceId());
                if (device.getMvnoId() != null && device.getMvnoId() == 1)
                    device.setMvnoId(deviceOptional.get().getMvnoId());
            } else {
                throw new IllegalArgumentException(
                        "You do not have access to update or delete this record.");
            }
        } else if (device.getDeviceProfileName() != null) {
            checkForUniqueDeviceName(device.getDeviceProfileName(), device.getMvnoId());
        }
        return device;
    }

    public Page<Device> getDevicePagebleList(Integer pageNumber, int customPageSize, String sortBy, Integer sortOrder,
                                             String searchText) {
        PageRequest pageRequest = null;
        if (sortOrder.equals(CommonConstants.SORT_ORDER_ASC))
            pageRequest = PageRequest.of(pageNumber - 1, customPageSize, Sort.by(sortBy));
        else
            pageRequest = PageRequest.of(pageNumber - 1, customPageSize, Sort.by(sortBy).descending());

        QDevice qDevice = QDevice.device;
        BooleanExpression exp = qDevice.isNotNull();

        if (!"".equals(searchText) && searchText != null) {
            exp = exp.and(qDevice.deviceProfileName.startsWithIgnoreCase(searchText))
                    .or(qDevice.description.startsWithIgnoreCase(searchText))
                    .or(qDevice.type.startsWithIgnoreCase(searchText));
        }
        Predicate builder = exp;
        return (Page<Device>) deviceRepository.findAll(builder, pageRequest);
    }

    private void checkForUniqueDeviceName(String deviceProfileName, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(deviceProfileName)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid device name.");
            }
            QDevice qDevice = QDevice.device;
            BooleanExpression boolExp = qDevice.isNotNull();
            boolExp = boolExp.and(qDevice.deviceProfileName.eq(deviceProfileName));
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qDevice.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            List<Device> deviceList = (List<Device>) deviceRepository.findAll(boolExp);

            if (!deviceList.isEmpty()) {
                throw new IllegalArgumentException("Device profile name : '" + deviceProfileName
                        + "' is already exist in the system, Please enter unique device profile name.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String changeDeviceStatus(String deviceProfileName, String status, Integer mvnoId, HttpServletRequest request) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(deviceProfileName)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG
                        + "Device profile name is mandatory. Please enter valid device profile name.");
            }
            QDevice qDevice = QDevice.device;
            BooleanExpression boolExp = qDevice.isNotNull();
            if (mvnoId != null && mvnoId == 1)
                boolExp = boolExp.and(qDevice.deviceProfileName.eq(deviceProfileName));
            else
                boolExp = boolExp.and(qDevice.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)).and(qDevice.deviceProfileName.eq(deviceProfileName)));

            Optional<Device> deviceOptional = deviceRepository.findOne(boolExp);
            if (!deviceOptional.isPresent()) {
                throw new IllegalArgumentException("No record found with device profile name : '" + deviceProfileName
                        + "'. Please enter valid device profile name.");
            } else {
                String oldststus = deviceOptional.get().getStatus();
                Device device = deviceOptional.get();
                device.setStatus(status);
                device.setLastModifiedOn(LocalDateTime.now());
                device.setLastModifiedBy(CommonConstants.USER_ADMIN);
                deviceRepository.save(device);
                String message = "";
                if (status.equals("Active")) {
                    message = "Device '" + device.getDeviceProfileName() + "' has been activated successfully.";
                } else {
                    message = "Device '" + device.getDeviceProfileName() + "' has been inactivated successfully.";
                }
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Device's Status has been updated  successfully with from" + oldststus + " to " + status + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                return message;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getDeviceData(Map<String, String> payload, Integer mvnoId, boolean isFromGetDeviceData) {
        Map<String, Object> result = new HashMap<>();
        String strUsername = "";
        String strPassword = "";
        String framedIpAdd = "";
        try {
//            //		System.out.println("Check device Profile: "+checkDeviceProfile("test@member","test@123", mvnoId));
            if (payload.get("username") != null && payload.get("password") != null) {
                strUsername = payload.get("username");
                strPassword = payload.get("password");
                Optional<Customers> customers = customersRepository.findByUsernameAndMvnoId(strUsername, mvnoId);
                if (!customers.isPresent()) {
                    throw new RuntimeException("Username not available");
                } else {
                    Customers customer = customers.get();
                    if (!customer.getPassword().equals(strPassword)) {
                        throw new RuntimeException("Input password is not match with username");
                    }
                    if (customer.getStatus().equals(CommonConstants.CUST_INACTIVE)) {
                        throw new RuntimeException("User In Inactive status");
                    }
                }
            }
            List<Device> devices = findAll(mvnoId);

            devices = devices.stream().filter(device -> RadiusConstants.ACTIVE.equalsIgnoreCase(device.getStatus())).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(devices)) {
                result.put("status", 404);
                return result;
            }
            Collections.sort(devices,
                    Comparator.comparing(Device::getPriority, Comparator.nullsFirst(Comparator.naturalOrder())));
            Device response = null;

            for (Device device : devices) {
                try {
                    String checkItem = device.getCheckItem();
                    if (checkItem.contains("||")) {
                        List<String> convertedList = Arrays.asList(checkItem.split("\\|\\|"));
                        if (checkOrCondition(payload, convertedList)) {
                            response = device;
                        }
                    } else if (checkItem.contains("&&")) {
                        List<String> convertedList = Arrays.asList(checkItem.split("\\&\\&"));
                        if (checkAndCondition(payload, convertedList)) {
                            response = device;
                        }
                    } else {
                        String str = "{" + checkItem.trim().replace("\"", "") + "}";
                        if (isJSONValid(str)) {
                            JSONObject json = new JSONObject(str.toLowerCase());
                            for (Map.Entry<String, String> entry : payload.entrySet()) {
                                if (json.has(entry.getKey().toLowerCase())) {
                                    if (((String) json.get(entry.getKey().toLowerCase()))
                                            .equalsIgnoreCase(entry.getValue())) {
                                        response = device;
                                    }
                                }
                            }
                        }
                    }

                    List<String> convertedList = Arrays.asList(checkItem.split("\\|\\|"));
                    for (String str : convertedList) {
                        str = "{" + str.trim().replace("\"", "") + "}";
                        for (Map.Entry<String, String> entry : payload.entrySet()) {
                            if (entry.getKey().toLowerCase().equalsIgnoreCase("username")) {
                                strUsername = entry.getValue();
                            }
                            if (entry.getKey().toLowerCase().equalsIgnoreCase("password")) {
                                strPassword = entry.getValue();
                            }
                            if (entry.getKey().toLowerCase().equalsIgnoreCase("Framed-ip-address")) {
                                framedIpAdd = entry.getValue();
                            }
                        }
                    }

                } catch (Exception e) {
                    continue;
                }

            }

            log.debug("Device Profile found: " + response);

            if (strUsername != null && strPassword != null) {
                //Process Radius Policy
                List<RadiusProfile> profileList = radiusProfileRepository.findAllByRequestTypeAndMvnoIdAndStatusOrderByPriorityDesc("Authentication", mvnoId, RadiusConstants.ACTIVE);
                RadiusProfile profile = null;
                AccessRequest request = new AccessRequest();
                request.addAttribute("User-Name", strUsername);
                for (RadiusProfile radiusProfile : profileList) {
                    ValidateExpression validate = new ValidateExpression();
                    boolean blnResponse = validate.checkExpression(radiusProfile.getCheckItem(), request, null);
                    log.info(String.format("Expression Check For %s : %s", radiusProfile.getName(), blnResponse));
                    if (blnResponse) {
                        profile = radiusProfile;
                        break;
                    }
                }
                if (profile != null) {
                    String deviceDriverName = profile.getDeviceDriverName();
                    if (deviceDriverName == null) {
                        deviceDriverName = CommonConstants.DEVICE_DRIVER_SAVBILL;
                    }
                    CustomerData custRetrunData = null;
                    String checkItem = profile.getCheckItem();
                    if (checkItem != null && checkItem.contains("User-Name=")) {
                        checkItem = checkItem.substring(checkItem.indexOf("=") + 1);
                    }
                    if (checkItem != null && checkItem.contains("@")) {
                        if (strUsername.contains("@")) {
                            strUsername = strUsername.substring(0, strUsername.lastIndexOf("@"));
                        }
                    }
                    log.info("Device driver for radius profile : " + deviceDriverName);
                    if (deviceDriverName.equalsIgnoreCase(CommonConstants.DEVICE_DRIVER_SAVBILL)) {
                        //skip for local db
                    } else {
                        DeviceDriverServiceImpl deviceDriverService = new DeviceDriverServiceImpl();
                        custRetrunData = deviceDriverService.isUserExist(deviceDriverName, strUsername, strPassword, mvnoId);
                        if (custRetrunData != null && custRetrunData.getUsername() != null) {
                            //continue as user authenticated
                            log.info(deviceDriverName + " Radius Profile found, Customer found: " + custRetrunData.getUsername());
                        } else {
                            log.info(deviceDriverName + " Radius Profile found, Customer not found!!");
                            result.put("status", 417);
                            result.put("error", "Use Not Found..!!");
                            return result;
                        }
                    }

                } else {
                    log.info(" Radius Profile not found!!");
                    result.put("status", 417);
                    result.put("error", " Radius Profile not found!!");
                    return result;
                }
            }
            if (response != null) {
                if (response.getType().equals("HTTP")) {
                    response = updateUrls(response, payload);
                } else if (response.getType().equalsIgnoreCase("COA")) {
                    if (isFromGetDeviceData) {
                        generateCoaDM(response.getCoaDmProfileId(), response.getMvnoId(), strUsername, payload.get("Framed-ip-address"));
                    } else {
                        generateCoaDMForCustomerLogin(response.getCoaDmProfileId(), response.getMvnoId(), strUsername, payload.get("Framed-ip-address"), strPassword);
                    }
                } else if (response.getType().equalsIgnoreCase("SNMP")) {
                    generateSNMP(response.getMvnoId(), strUsername, payload.get("Framed-ip-address"), true, false);
                }
                result.put("device", response);
                result.put("status", "200");
                return result;
            }
            result.put("status", 500);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", 500);
            result.put("errorMessage", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> getDeviceDataForLogin(Map<String, String> payload, Integer mvnoId, boolean isFromGetDeviceData) {
        Map<String, Object> result = new HashMap<>();
        String strUsername = "";
        String strPassword = "";
        try {
            Customers customer = null;
            if (payload.get("username") != null && payload.get("password") != null) {
                strUsername = payload.get("username");
                strPassword = payload.get("password");
                Optional<Customers> customers = customersRepository.findByUsernameAndMvnoId(strUsername, mvnoId);
                if (!customers.isPresent()) {
                    throw new RuntimeException("Username not available");
                } else {
                    customer = customers.get();
                    if (!customer.getPassword().equals(strPassword)) {
                        throw new RuntimeException("Input password is not match with username");
                    }
                    if (customer.getStatus().equalsIgnoreCase(CommonConstants.CUST_INACTIVE)) {
                        throw new RuntimeException("User In Inactive status");
                    }
                }
            }

            Device response = null;
            String userIP = payload.get("Framed-ip-address");

            LiveUserSearchDTO paginationDTO = new LiveUserSearchDTO();
            paginationDTO.setFramedIpAddress(userIP);
            Page<LiveUser> liveUsersFromIp = liverUserService.findLiveUsersUsingFilter(paginationDTO, mvnoId);
            List<LiveUser> liveUsersList = new ArrayList<>(liveUsersFromIp.getContent());


            if (!CollectionUtils.isEmpty(liveUsersList)) {
                LiveUser liveuser = liveUsersList.get(0);
                //Check Faulty mac user
                log.warn("Find Live USer: " + liveuser.getUserName() + ", sessionId: " + liveuser.getAcctSessionId() + ", framed Ip addrss: " + userIP);
                if (liveuser.getUserName().equalsIgnoreCase(strUsername) ||
                        (liveuser.getFramedIpAddress().equalsIgnoreCase(userIP) && (liveuser.getCustid() != null && !liveuser.getCustid().trim().isEmpty() && !liveuser.getCustid().equalsIgnoreCase("0")))) {
                    CacheRetrival cacheRetrival = new CacheRetrival();
                    Map<String, FaultyMAC> faultyMACS = cacheRetrival.getFaultyMacList();
                    RadiusUtility radiusUtility = new RadiusUtility();
                    String normalizedMac = radiusUtility.normalizeMacAddress(liveuser.getUserName());
                    if (faultyMACS.get(normalizedMac) != null) {
                        //skip
                        log.info("Faulty MAc Found in live session so Skip duplicate check: " + liveuser.getUserName());
                    } else {
                        log.error("Duplicate Login attempt by userName: " + strUsername + ": framed-ip: " + liveuser.getFramedIpAddress());
                        result.put("status", 412);
                        result.put("error", "Duplicate Login attempt by userName: " + strUsername + ": framed-ip: " + liveuser.getFramedIpAddress());
                        return result;
                    }
                }
                DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                CustomerData custRetrunData = dbAuth.getDBCustomer(customer.getUsername(), customer.getMvnoId(), String.valueOf(customer.getId()), null, false);
                RadiusUtility radiusUtility = new RadiusUtility();
                AccountingRequest acctReq = (AccountingRequest) radiusUtility.getRequestFromLiveUser(liveuser);
                Client clientData = radiusUtility.identifyClient(liveuser.getNasIpAddress(), acctReq);
                if (clientData.getClientGroupData() != null) {
                    // skip if data available
                } else {
                    clientData = clientService.updateRadiusClientData(clientData, acctReq);
                }
                ClientGroup cltGroupData = clientData.getClientGroupData();
                log.info("cltGroupData Found For Login: " + cltGroupData.getName());
                //Check Radius Group validation #SUP: SUP-1355
                List<DynamicAttributeMapping> dynamicAttributeMappingList = cltGroupData.getDynamicAttributeMappings();
                if (dynamicAttributeMappingList != null) {
                    RadiusPacket accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, acctReq.getPacketIdentifier());
                    radiusUtility.validateDynamicAttribute(clientData, dynamicAttributeMappingList, custRetrunData, acctReq, accessResponse, "");
                } else {
                    log.debug("No Authorization Attribute Configured");
                }
                if (custRetrunData.getStrReplyMessage() != null && custRetrunData.getStrReplyMessage().contains("Validation Fail")) {
                    log.error("Validation fail for dynamic attributes: " + liveuser.getAcctSessionId());
                    throw new RuntimeException("Invalid location lock");
                }
                //verify vlan with attribute configuration in radius profile
                //Check Radius Group validation #SUP: SUP-1355
                try {
                    if (!CollectionUtils.isEmpty(cltGroupData.getVlanProfileMapping())) {
                        VLANManagement vlanManagement = radiusUtility.verifyVlan1(acctReq, custRetrunData, cltGroupData, clientData.getMvnoId());
                        if (vlanManagement != null) {
                            log.info(String.format("Vlan Matched for username: %s, vlan name: %s", acctReq.getAttribute("User-Name").getAttributeValue(), vlanManagement.getVlanName()));
                            vlanManagement.setLastAuthMatched(LocalDateTime.now());
                            DBAccountingDriver dbAccountingDriver = new DBAccountingDriver();
                            dbAccountingDriver.updatevlanManagement(vlanManagement);// IF Vlan is matched than update last Auth Matched value.
                        } else if (cltGroupData.isVlanCheckRequired()) {
                            log.info(String.format("Vlan Not Matched for username: %s", acctReq.getAttribute("User-Name").getAttributeValue()));
                            throw new RuntimeException("Invalid location lock");
                        }
                    } else {
                        log.info("Vlan attribute not configured so skip vlan validation");
                    }
                } catch (Exception ex) {
                    log.error("Error while vlan validation: " + ex.getMessage());
                    throw new RuntimeException("Invalid location lock");
                }
                Device device = new Device();
                if (clientData.getDeviceId() != null) {
                    device = deviceRepository.findById(clientData.getDeviceId()).orElse(null);
                }
                if (device != null && device.getDeviceProfileName() != null) {
                    response = device;
                } else {
                    result.put("status", 417);
                    result.put("error", " Device not found!!");
                    return result;
                }
            }
//            else {
//                log.error("Live user not found for Framed Ip Address: " + liveUsersFromIp);
//                result.put("status", 417);
//                result.put("error", "No Records Found in session table for give IPAddress.");
//                return result;
//            }
            log.debug("Device Profile found: " + response);

            if (strUsername != null && strPassword != null) {
                //Process Radius Policy
                List<RadiusProfile> profileList = radiusProfileRepository.findAllByRequestTypeAndMvnoIdAndStatusOrderByPriorityDesc("Authentication", mvnoId, RadiusConstants.ACTIVE);
                RadiusProfile profile = null;
                AccessRequest request = new AccessRequest();
                request.addAttribute("User-Name", strUsername);
                for (RadiusProfile radiusProfile : profileList) {
                    ValidateExpression validate = new ValidateExpression();
                    boolean blnResponse = validate.checkExpression(radiusProfile.getCheckItem(), request, null);
                    log.info(String.format("Expression Check For %s : %s", radiusProfile.getName(), blnResponse));
                    if (blnResponse) {
                        profile = radiusProfile;
                        break;
                    }
                }
                if (profile != null) {
                    String deviceDriverName = profile.getDeviceDriverName();
                    if (deviceDriverName == null) {
                        deviceDriverName = CommonConstants.DEVICE_DRIVER_SAVBILL;
                    }
                    CustomerData custRetrunData = null;
                    String checkItem = profile.getCheckItem();
                    if (checkItem != null && checkItem.contains("User-Name=")) {
                        checkItem = checkItem.substring(checkItem.indexOf("=") + 1);
                    }
                    if (checkItem != null && checkItem.contains("@")) {
                        if (strUsername.contains("@")) {
                            strUsername = strUsername.substring(0, strUsername.lastIndexOf("@"));
                        }
                    }
                    log.info("Device driver for radius profile : " + deviceDriverName);
                    if (deviceDriverName.equalsIgnoreCase(CommonConstants.DEVICE_DRIVER_SAVBILL)) {
                        //skip for local db
                    } else {
                        DeviceDriverServiceImpl deviceDriverService = new DeviceDriverServiceImpl();
                        custRetrunData = deviceDriverService.isUserExist(deviceDriverName, strUsername, strPassword, mvnoId);
                        if (custRetrunData != null && custRetrunData.getUsername() != null) {
                            //continue as user authenticated
                            log.info(deviceDriverName + " Radius Profile found, Customer found: " + custRetrunData.getUsername());
                        } else {
                            log.info(deviceDriverName + " Radius Profile found, Customer not found!!");
                            result.put("status", 417);
                            result.put("error", "Use Not Found..!!");
                            return result;
                        }
                    }

                } else {
                    log.info(" Radius Profile not found!!");
                    result.put("status", 417);
                    result.put("error", " Radius Profile not found!!");
                    return result;
                }
            }
            if (response != null) {
                if (response.getType().equals("HTTP")) {
                    response = updateUrls(response, payload);
                } else if (response.getType().equalsIgnoreCase("COA")) {
                    if (isFromGetDeviceData) {
                        generateCoaDM(response.getCoaDmProfileId(), response.getMvnoId(), strUsername, payload.get("Framed-ip-address"));
                    } else {
                        generateCoaDMForCustomerLogin(response.getCoaDmProfileId(), response.getMvnoId(), strUsername, payload.get("Framed-ip-address"), strPassword);
                    }
                } else if (response.getType().equalsIgnoreCase("SNMP")) {
                    generateSNMP(response.getMvnoId(), strUsername, payload.get("Framed-ip-address"), true, false);
                }
                result.put("device", response);
                result.put("status", "200");
                return result;
            }
            result.put("status", 500);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", 500);
            result.put("errorMessage", e.getMessage());
        }
        return result;
    }

    public boolean checkDeviceProfile(String strUsername, String strPassword, Integer mvnoId) {
        //Process Radius Policy
        CacheRetrival cacheRetrival = new CacheRetrival();

        List<RadiusProfile> profileList = radiusProfileRepository.findAllByRequestTypeAndMvnoIdAndStatusOrderByPriorityDesc("Authentication", mvnoId, RadiusConstants.ACTIVE);

        List<RadiusProfile> profileList1 = radiusProfileRepository.findAllByRequestTypeAndMvnoId("Authentication", mvnoId);
        RadiusProfile profile = null;
        AccessRequest request = new AccessRequest();
        request.addAttribute("User-Name", strUsername);
        for (RadiusProfile radiusProfile : profileList) {
            ValidateExpression validate = new ValidateExpression();
            boolean blnResponse = validate.checkExpression(radiusProfile.getCheckItem(), request, null);
            log.info(String.format("Expression Check For %s : %s", radiusProfile.getName(), blnResponse));
            if (blnResponse) {
                profile = radiusProfile;
                break;
            }
        }
        if (profile != null) {
            String deviceDriverName = profile.getDeviceDriverName();
            if (deviceDriverName == null) {
                deviceDriverName = CommonConstants.DEVICE_DRIVER_SAVBILL;
            }
            CustomerData custRetrunData = null;
            String checkItem = profile.getCheckItem();
            if (checkItem != null && checkItem.contains("User-Name=")) {
                checkItem = checkItem.substring(checkItem.indexOf("=") + 1);
            }
            if (checkItem != null && checkItem.contains("@")) {
                if (strUsername.contains("@")) {
                    strUsername = strUsername.substring(0, strUsername.lastIndexOf("@"));
                }
            }
            if (deviceDriverName.equalsIgnoreCase(CommonConstants.DEVICE_DRIVER_SAVBILL)) {
                //skip for local db
            } else {
                DeviceDriverServiceImpl deviceDriverService = new DeviceDriverServiceImpl();
                custRetrunData = deviceDriverService.isUserExist(deviceDriverName, strUsername, strPassword, mvnoId);
                if (custRetrunData != null && custRetrunData.getUsername() != null) {
                    //continue as user authenticated
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;
    }

    @Override
    public Integer countByCoaDmProfileId(Long coaDmProfileId) {
        QDevice qDevice = QDevice.device;
        BooleanExpression exp = qDevice.isNotNull();
        exp = exp.and(qDevice.coaDmProfileId.eq(coaDmProfileId));
        List<Device> devices = (List<Device>) deviceRepository.findAll(exp);
        return devices.size();
    }

    public boolean checkAndCondition(Map<String, String> payload, List<String> convertedList) {
        int counter = 0;
        boolean isOr = true;
        for (String str : convertedList) {
            if (str.contains("&&")) {
                List<String> strList = Arrays.asList(str.split("\\&\\&"));
                isOr = checkAndCondition(payload, strList);
                continue;
            }
            str = "{" + str.trim().replace("\"", "") + "}";
            if (isJSONValid(str)) {
                JSONObject json = new JSONObject(str.toLowerCase());
                for (Map.Entry<String, String> entry : payload.entrySet()) {
                    if (json.has(entry.getKey().toLowerCase())) {
                        if (((String) json.get(entry.getKey().toLowerCase())).equalsIgnoreCase(entry.getValue())) {
                            counter++;
                        }
                    }
                }
            }
        }
        if (counter == convertedList.size())
            return true && isOr;
        else
            return false;
    }

    public boolean checkOrCondition(Map<String, String> payload, List<String> convertedList) {
        boolean isAnd = false;
        for (String str : convertedList) {
            if (str.contains("&&")) {
                List<String> strList = Arrays.asList(str.split("\\&\\&"));
                isAnd = checkAndCondition(payload, strList);
                continue;
            }
            str = "{" + str.trim().replace("\"", "") + "}";
            if (isJSONValid(str)) {
                JSONObject json = new JSONObject(str.toLowerCase());
                for (Map.Entry<String, String> entry : payload.entrySet()) {
                    if (json.has(entry.getKey().toLowerCase())) {
                        if (((String) json.get(entry.getKey().toLowerCase())).equalsIgnoreCase(entry.getValue())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false || isAnd;
    }

    public Device updateUrls(Device device, Map<String, String> payload) {
        String logInUrl = device.getLoginurl().trim();
        String logOutUrl = device.getLogouturl().trim();
        for (Map.Entry<String, String> entry : payload.entrySet()) {
            if (logInUrl.contains(entry.getKey())) {
                logInUrl = logInUrl.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            if (logOutUrl.contains(entry.getKey())) {
                logOutUrl = logOutUrl.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        device.setLoginurl(logInUrl);
        device.setLogouturl(logOutUrl);
        return device;
    }

    public static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = new HashMap<String, String>();
        if (query.contains("&")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.contains("=")) {
                    String name = param.split("=")[0];
                    String value = param.split("=")[1];
                    map.put(name, value);
                }
            }
        }
        return map;

    }

    public int generateCoaDM(Long coaDMProfileId, Integer mvnoId, String username, String userIP) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        Integer responseCode = RadiusConstants.FAIL;
        log.debug("In the Generate CoA/DM");
        try {
            LiveUserSearchDTO paginationDTO = new LiveUserSearchDTO();

            //Getting CoA DM Profile for Packet
            CoaDMProfile coaProfileData = coaDMProfileService.findCoaDMProfileById(coaDMProfileId, mvnoId);
            log.info("Generate CoA/DM Using Profile:" + coaProfileData.getName() + ":");
            List<CoaDMProfileAttribute> coaDMProfileAttributes = coaDMProfileAttributeService.findCoaDMProfileAttributeByCoaDMProfileId(coaDMProfileId, mvnoId);
            coaProfileData.setCoaDMProfileAttributeList(coaDMProfileAttributes);
            Optional<Customers> customers = customersRepository.findByUsernameAndMvnoId(username, mvnoId);
            //Get detail of Live Users
            paginationDTO.setUserName(username);
            Page<LiveUser> liveUsers = liverUserService.findLiveUsersUsingFilter(paginationDTO, mvnoId);
            List<LiveUser> liveUsersList = new ArrayList<>(liveUsers.getContent());  // Create a new modifiable list
            if (customers.isPresent()) {
                List<MacAddressMapping> macAddressMappings = macAddressMappingRepository.findMacAddressMappingByCustomerId(Long.valueOf((customers.get().getId())));
                if (!CollectionUtils.isEmpty(macAddressMappings)) {
                    log.debug("Add session with Mac for COA/DM: " + macAddressMappings.size());
                    for (MacAddressMapping mapping : macAddressMappings) {
                        log.debug("Add session with Mac for COA/DM MAc: " + mapping.getMacAddress());
                        paginationDTO = new LiveUserSearchDTO();
                        paginationDTO.setUserName(mapping.getMacAddress());
                        Page<LiveUser> LiveUsers = liveUserService.findLiveUsersUsingFilter(paginationDTO, null /*mvnoId*/);
                        if (!LiveUsers.isEmpty()) {
                            if (!CollectionUtils.isEmpty(liveUsersList))
                                liveUsersList.addAll(LiveUsers.getContent());
                            else
                                liveUsersList = new ArrayList<>(LiveUsers.getContent());
                        }
                    }
                }
            } else {
                throw new RuntimeException("Customer not found for given Device Profile!");
            }
            if (userIP != null && !userIP.isEmpty()) {
                //Get detail of Live Users from framed-ip-address
                paginationDTO = new LiveUserSearchDTO();
                paginationDTO.setFramedIpAddress(userIP);
                Page<LiveUser> liveUsersFromIp = liverUserService.findLiveUsersUsingFilter(paginationDTO, mvnoId);
                if (!liveUsersFromIp.isEmpty()) {
                    List<LiveUser> users = liveUsersFromIp.getContent();
                    if (!CollectionUtils.isEmpty(liveUsersList) && !CollectionUtils.isEmpty(users)) {
                        List<Long> userIds = liveUsersList.stream().map(LiveUser::getCdrID).collect(Collectors.toList());
                        users = users.stream().filter(u -> !userIds.contains(u.getCdrID())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(users))
                            liveUsersList.addAll(users);
                    } else if (!CollectionUtils.isEmpty(users))
                        liveUsersList = users;
                }
            }

            //find by class
            List<LiveUser> liveUsersByClass = liveUserService.findLiveUsersByLClass(username);
            if (!CollectionUtils.isEmpty(liveUsersList) && !CollectionUtils.isEmpty(liveUsersByClass)) {
                List<Long> userIds = liveUsersList.stream().map(LiveUser::getCdrID).collect(Collectors.toList());
                liveUsersByClass = liveUsersByClass.stream().filter(u -> !userIds.contains(u.getCdrID())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(liveUsersByClass))
                    liveUsersList.addAll(liveUsersByClass);
            } else if (!CollectionUtils.isEmpty(liveUsersByClass))
                liveUsersList = liveUsersByClass;

            if (CollectionUtils.isEmpty(liveUsersList)) {
                log.info("No of Live User Found:" + liveUsers.getSize() + ":");
            } else {
                response.put("liveUser", liveUsers);
                for (int j = 0; j < liveUsersList.size(); j++) {
                    LiveUser liveuser = liveUsersList.get(j);
                    List<Client> clientList = clientService.findClientByIpAddress(liveuser.getNasIpAddress(), mvnoId);
                    log.info("NAS IP Found : " + liveuser.getNasIpAddress() + ":UserIP:" + liveuser.getFramedIpAddress() + ":User:" + liveuser.getUserName() + ":");
                    RadiusUtility radiusUtility = new RadiusUtility();
                    AccountingRequest acctReq = (AccountingRequest) radiusUtility.getRequestFromLiveUser(liveuser);
                    for (int i = 0; i < clientList.size(); i++) {
                        Client clientData = clientList.get(i);
                        if (clientData.getClientGroupData() != null) {
                            // skip if data available
                        } else {
                            clientData = clientService.updateRadiusClientData(clientData, acctReq);
                        }
                        ClientGroup cltGroupData = clientData.getClientGroupData();
                        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                        CustomerData custRetrunData = dbAuth.getDBCustomer(null, customers.get().getMvnoId(), String.valueOf(customers.get().getId()), null, false);
                        //Check Radius Group validation #SUP: SUP-1355
                        List<DynamicAttributeMapping> dynamicAttributeMappingList = clientData.getClientGroupData().getDynamicAttributeMappings();
                        if (dynamicAttributeMappingList != null) {
                            RadiusPacket accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, acctReq.getPacketIdentifier());
                            radiusUtility.validateDynamicAttribute(clientData, dynamicAttributeMappingList, custRetrunData, acctReq, accessResponse, "");
                        } else {
                            log.debug("No Authorization Attribute Configured");
                        }
                        if (custRetrunData.getStrReplyMessage() != null && custRetrunData.getStrReplyMessage().contains("Validation Fail")) {
                            throw new RuntimeException("Invalid location lock");
                        }
                        //verify vlan with attribute configuration in radius profile
                        ////Check Radius Group validation #SUP: SUP-1355
                        try {
                            if (!CollectionUtils.isEmpty(cltGroupData.getVlanProfileMapping())) {
                                VLANManagement vlanManagement = radiusUtility.verifyVlan1(acctReq, custRetrunData, cltGroupData, clientData.getMvnoId());
                                if (vlanManagement != null) {
                                    log.info(String.format("Vlan Matched for username: %s, vlan name: %s", acctReq.getAttribute("User-Name").getAttributeValue(), vlanManagement.getVlanName()));
                                    vlanManagement.setLastAuthMatched(LocalDateTime.now());
                                    DBAccountingDriver dbAccountingDriver = new DBAccountingDriver();
                                    dbAccountingDriver.updatevlanManagement(vlanManagement);// IF Vlan is matched than update last Auth Matched value.
                                } else if (cltGroupData.isVlanCheckRequired()) {
                                    log.info(String.format("Vlan Not Matched for username: %s", acctReq.getAttribute("User-Name").getAttributeValue()));
                                    throw new RuntimeException("Invalid location lock");
                                }
                            } else {
                                log.info("Vlan attribute not configured so skip vlan validation");
                            }
                        } catch (Exception ex) {
                            log.error("Error while vlan validation: " + ex.getMessage());
                            throw new RuntimeException("Invalid location lock");
                        }
                        //Now Actual CoA Firing Start
                        if (coaProfileData != null && clientData != null) {
                            RadiusPacket coaDMResponse = null;
                            try {
                                if (coaProfileData != null) {
                                    coaProfileData.setGateway(liveuser.getNasIpAddress());
                                    log.info("DM Profile Found : " + coaProfileData.getName() + ":Type:" + coaProfileData.getType());
                                    log.info("CoA/DM Firing on:" + coaProfileData.getGateway() + ":Key:" + clientData.getSharedKey() + ":Port:" + coaProfileData.getPort());

                                    //Need to work for customer data
                                    try {
                                        if (custRetrunData.getMacAuthEnable() != null && !custRetrunData.getMacAuthEnable()) {
                                            //check faulty mac
                                            CacheRetrival cacheRetrival = new CacheRetrival();
                                            List<String> faultyMACS = cacheRetrival.getFaultyMacData();
                                            boolean faultyMacNotFound = true;
                                            if (!CollectionUtils.isEmpty(faultyMACS)) {
                                                String normalizedTargetMac = liveuser.getCallingStationId();
                                                if (normalizedTargetMac != null) {
                                                    normalizedTargetMac = radiusUtility.normalizeMacAddress(liveuser.getCallingStationId());
                                                }
                                                boolean isPresent = faultyMACS.stream()
                                                        .map(radiusUtility::normalizeMacAddress) // Use instance method reference
                                                        .anyMatch(normalizedTargetMac::equals);
                                                if (isPresent) {
//                                                if (faultyMACS.contains(liveuser.getCallingStationId())) {
                                                    faultyMacNotFound = false;
                                                    log.error("Faulty Mac Found, So skipp Store Mac: " + liveuser.getCallingStationId());
                                                }
                                            }
                                            if (faultyMacNotFound) {
                                                //save mac in cache
                                                CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                                CustomerDetails customerDetails = new CustomerDetails(custRetrunData.getUsername(), liveuser.getCustid(), liveuser.getlClass());
                                                cacheService.put(radiusUtility.normalizeMacAddress(liveuser.getCallingStationId()), customerDetails);
                                                log.info("COA Cache for: " + customerDetails);
                                            }
                                        }
                                    } catch (Exception ex) {
                                        log.debug("Exception to store mac and username in cache: " + ex.getMessage());
                                        ex.printStackTrace();
                                    }
                                    coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, acctReq, username, custRetrunData, liveuser.getNasIpAddress());
                                    log.warn("COA/DM response:"+coaDMResponse+":For Event:"+coaProfileData.getName()+":");
                                    if(coaDMResponse!=null){
                                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),acctReq.getUserName(), String.valueOf(coaDMResponse.getPacketType()),coaProfileData.getName(), clientData.getMvnoId(),coaDMResponse );
                                    }
                                    else{
                                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),acctReq.getUserName(), "Timeout or Error Occurs",coaProfileData.getName(), clientData.getMvnoId(),coaDMResponse);
                                    }
                                } else {
                                    log.info("CoA/DM Profile Not Found Skipping CoA/DM");
                                }
                            } catch (Exception e) {
                                RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                                radaysn.coaRespnseProcess(coaProfileData.getGateway(),acctReq.getUserName(), "Error or Timeout",coaProfileData.getName(), clientData.getMvnoId(),coaDMResponse);
                                log.error("CoA/DM Failed:" + e.getMessage());
                                log.info("CoA/DM Failed:" + e.getMessage());
                            }
                        } else {
                            log.info("Client Not Found Skipping CoA/DM");
                        }
                    }
                }

            }

            responseCode = RadiusConstants.SUCCESS;
            return responseCode;
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error("Error while fetching CoaDMProfiles by id: " + coaDMProfileId + " " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
//            return responseCode;
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }


    public int generateCoaDMForCustomerLogin(Long coaDMProfileId, Integer mvnoId, String username, String userIP, String password) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        Integer responseCode = RadiusConstants.FAIL;
        RadiusUtility radiusUtility = new RadiusUtility();
        DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
        log.debug("In the Generate CoA/DM for login and logout");
        try {
            //Getting CoA DM Profile for Packet
            Optional<Customers> customers = customersRepository.findByUsernameAndMvnoId(username, mvnoId);
            if (!customers.isPresent()) {
                throw new RuntimeException("Username not available");
            } else {
                Customers customer = customers.get();
                if (!customer.getPassword().equals(password)) {
                    throw new RuntimeException("Input password is not match with username");
                }
                if (customer.getStatus().equals(CommonConstants.CUST_INACTIVE)) {
                    throw new RuntimeException("User In Inactive status");
                }

            }

            LiveUserSearchDTO paginationDTO = new LiveUserSearchDTO();
            paginationDTO = new LiveUserSearchDTO();
            paginationDTO.setFramedIpAddress(userIP);
            CustomerData custRetrunData = dbAuth.getDBCustomer(customers.get().getUsername(), customers.get().getMvnoId(), String.valueOf(customers.get().getId()), null, false);
            log.info("custRetrunData: " + custRetrunData.getUsername());
            Page<LiveUser> liveUsersFromIp = liverUserService.findLiveUsersUsingFilter(paginationDTO, mvnoId);
            Optional<LiveUser> loginSession = null;
            if (liveUsersFromIp != null && !liveUsersFromIp.isEmpty() && liveUsersFromIp.getContent().size() > 0) {
                List<LiveUser> liveUsersForLogin = new ArrayList<>(liveUsersFromIp.getContent());
                loginSession = liveUsersForLogin.stream().min(Comparator.comparing(LiveUser::getLastmodifiedDate));
                AccountingRequest acctReq = (AccountingRequest) radiusUtility.getRequestFromLiveUser(loginSession.get());
                Client clientData = radiusUtility.identifyClient(loginSession.get().getNasIpAddress(), acctReq);
                if (clientData.getClientGroupData() != null) {
                    // skip if data available
                } else {
                    clientData = clientService.updateRadiusClientData(clientData, acctReq);
                }
                //Check Radius Group validation #SUP: SUP-1355
                List<DynamicAttributeMapping> dynamicAttributeMappingList = clientData.getClientGroupData().getDynamicAttributeMappings();
                if (dynamicAttributeMappingList != null) {
                    RadiusPacket accessResponse = new RadiusPacket(AAAConstant.ACCESS_REJECT, acctReq.getPacketIdentifier());
                    radiusUtility.validateDynamicAttribute(clientData, dynamicAttributeMappingList, custRetrunData, acctReq, accessResponse, "");
                } else {
                    log.debug("No Authorization Attribute Configured");
                }//nasportid Validation Fail
                if (custRetrunData.getStrReplyMessage() != null && custRetrunData.getStrReplyMessage().contains("Validation Fail")) {
                    throw new RuntimeException("Invalid location lock");
                }
                ClientGroup cltGroupData = clientData.getClientGroupData();
                log.info("cltGroupData Found For Login: " + cltGroupData.getName());

                //verify vlan with attribute configuration in radius profile
                ////Check Radius Group validation #SUP: SUP-1355
                try {
                    if (!CollectionUtils.isEmpty(cltGroupData.getVlanProfileMapping())) {
                        VLANManagement vlanManagement = radiusUtility.verifyVlan1(acctReq, custRetrunData, cltGroupData, clientData.getMvnoId());
                        if (vlanManagement != null) {
                            log.info(String.format("Vlan Matched for username: %s, vlan name: %s", acctReq.getAttribute("User-Name").getAttributeValue(), vlanManagement.getVlanName()));
                            vlanManagement.setLastAuthMatched(LocalDateTime.now());
                            DBAccountingDriver dbAccountingDriver = new DBAccountingDriver();
                            dbAccountingDriver.updatevlanManagement(vlanManagement);// IF Vlan is matched than update last Auth Matched value.
                        } else if (cltGroupData.isVlanCheckRequired()) {
                            log.info(String.format("Vlan Not Matched for username: %s", acctReq.getAttribute("User-Name").getAttributeValue()));
                            throw new RuntimeException("Invalid location lock");
                        }
                    } else {
                        log.info("Vlan attribute not configured so skip vlan validation");
                    }
                } catch (Exception ex) {
                    log.error("Error while vlan validation: " + ex.getMessage());
                    throw new RuntimeException("Invalid location lock");
                }
            }

            List<LiveUser> liveUsersList = liveUserService.findAllLiveUserByCustId(String.valueOf(customers.get().getId()));//new ArrayList<>(liveUsersFromIp.getContent());
            if (CollectionUtils.isEmpty(liveUsersList)) {
                log.info("No of Live User Found For Logout:" + liveUsersList.size());
            } else {
                log.info("Process COA/DM for logout customer termination");
                List<Long> cltGrpIdsLong = liveUsersList.stream()
                        .map(LiveUser::getClientGroupId) // Extracts the String client group ID
                        .map(Long::valueOf)             // Converts each String to Long
                        .collect(Collectors.toList());
                List<ClientGroup> clientGroupList = clientGroupService.getCltGroupByIdsAndConcurrencyAndSessionLogout(cltGrpIdsLong, true, true);
                List<String> cltGrpstr = clientGroupList.stream().map(ClientGroup::getClientGroupId).map(String::valueOf).collect(Collectors.toList());
                List<LiveUser> filteredLiveUsers = liveUsersList.stream()
                        .filter(liveUser -> liveUser.getClientGroupId() != null && cltGrpstr.contains(liveUser.getClientGroupId()))
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(filteredLiveUsers)) {
                    response.put("liveUser", filteredLiveUsers);
                    LiveUser liveuser = filteredLiveUsers.get(0);
                    log.info("NAS IP Found : " + liveuser.getNasIpAddress() + ":UserIP:" + liveuser.getFramedIpAddress() + ":User:" + liveuser.getUserName() + ":");
                    AccountingRequest acctReq = (AccountingRequest) radiusUtility.getRequestFromLiveUser(liveuser);
                    Client clientData = radiusUtility.identifyClient(liveuser.getNasIpAddress(), acctReq);
                    log.info("clientData Found: " + clientData.getClientIpAddress());
//                for (int i = 0; i < clientList.size(); i++) {
//                    Client clientData = clientList;
                    if (clientData.getClientGroupData() != null) {
                        // skip if data available
                    } else {
                        clientData = clientService.updateRadiusClientData(clientData, acctReq);
                    }
                    ClientGroup cltGroupData = clientData.getClientGroupData();
                    log.info("cltGroupData Found For Logout: " + cltGroupData.getName());

                    if (clientData != null) {
                        try {
                            try {
                                if (custRetrunData.getMacAuthEnable() != null && !custRetrunData.getMacAuthEnable()) {
                                    //check faulty mac
                                    CacheRetrival cacheRetrival = new CacheRetrival();
                                    List<String> faultyMACS = cacheRetrival.getFaultyMacData();
                                    boolean faultyMacNotFound = true;
                                    if (!CollectionUtils.isEmpty(faultyMACS)) {
                                        String normalizedTargetMac = liveuser.getCallingStationId();
                                        if (normalizedTargetMac != null) {
                                            normalizedTargetMac = radiusUtility.normalizeMacAddress(liveuser.getCallingStationId());
                                        }
                                        boolean isPresent = faultyMACS.stream()
                                                .map(radiusUtility::normalizeMacAddress) // Use instance method reference
                                                .anyMatch(normalizedTargetMac::equals);
                                        if (isPresent) {
                                            faultyMacNotFound = false;
                                            log.error("Faulty Mac Found, So skipp Store Mac: " + liveuser.getCallingStationId());
                                        }
                                    }
                                    if (faultyMacNotFound) {
                                        //save mac in cache
                                        CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                        CustomerDetails customerDetails = new CustomerDetails(custRetrunData.getUsername(), liveuser.getCustid(), liveuser.getlClass());
                                        cacheService.put(radiusUtility.normalizeMacAddress(liveuser.getCallingStationId()), customerDetails);
                                        log.info("COA Cache for: " + customerDetails);
                                    }
                                }
                            } catch (Exception ex) {
                                log.debug("Exception to store mac and username in cache: " + ex.getMessage());
                                ex.printStackTrace();
                            }
//                            radiusUtility.initiateCoADM(coaProfileData, acctReq, username, custRetrunData);

                        } catch (Exception e) {
                            log.info("CoA/DM Failed:" + e.getMessage());
                        }
                    } else {
                        log.info("Client Not Found Skipping CoA/DM");
                    }

                    boolean isConcurrenyExist = checkCustomerConcurrency(liveuser.getUserName(), liveuser.getCallingStationId(), cltGroupData.getClientGroupId(), custRetrunData);
                    if (!isConcurrenyExist) {
                        if (cltGroupData.isLogoutOldSessionOnNew()) {
                            log.info("Checking Live user for Logout Event on concurrency fail..!");
                            LiveUser user = filteredLiveUsers.get(0);
                            log.info("First User To Logout: " + user.getAcctSessionId() + " user: " + user.getUserName());
                            changeUserData changeUserData = new changeUserData(user.getUserName(),
                                    Long.valueOf(user.getMvnoId()));
                            log.info("Live User found for Trigger CUSOTMER_TERMINATION liveUser session Id:" + user.getAcctSessionId());
                            List<changeUserData> userList = new ArrayList<changeUserData>();
                            userList.add(changeUserData);
                            String event = CommonConstants.CoaDmResonContant.TERMINATE_SESSION;
                            customerService.triggerCOADMForSingleLiveSession(user, event, "COA", custRetrunData);
                        }
                    }
                } else {
                    log.info("Live USer Not found For Customer Termination");
                }

            }
            CoaDMProfile coaProfileData = coaDMProfileService.findCoaDMProfileById(coaDMProfileId, mvnoId);
            log.info("Generate CoA/DM Using Profile:" + coaProfileData.getName() + ":");
            if (coaProfileData != null) {
                List<CoaDMProfileAttribute> coaDMProfileAttributes = coaDMProfileAttributeService.findCoaDMProfileAttributeByCoaDMProfileId(coaDMProfileId, mvnoId);
                coaProfileData.setCoaDMProfileAttributeList(coaDMProfileAttributes);
                if (loginSession != null && loginSession.isPresent()) {
                    log.info("Process COA/DM for login on device profile: " + coaProfileData.getName() + " Using frame Ip: " + userIP);
                    coaProfileData.setGateway(loginSession.get().getNasIpAddress());
                    log.info("DM Profile Found : " + coaProfileData.getName() + ":Type:" + coaProfileData.getType());
                    log.info("CoA/DM Firing on:" + coaProfileData.getGateway() + ":Port:" + coaProfileData.getPort());
                    log.info("Live session found for Login COA/DM Session: " + loginSession.get().getAcctSessionId() + " username: " + loginSession.get().getUserName());
                    AccountingRequest acctReqForLogin = (AccountingRequest) radiusUtility.getRequestFromLiveUser(loginSession.get());
                    RadiusPacket coaDMResponse = null;
                    try {
                        coaDMResponse = radiusUtility.initiateCoADM(coaProfileData, acctReqForLogin, username, custRetrunData, loginSession.get().getNasIpAddress());
                        if(coaProfileData!=null){
                            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                            radaysn.coaRespnseProcess(coaProfileData.getGateway(),acctReqForLogin.getUserName(), String.valueOf(coaDMResponse.getPacketType()),coaProfileData.getName(), mvnoId,coaDMResponse);
                        }
                        else{
                            RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                            radaysn.coaRespnseProcess(coaProfileData.getGateway(),acctReqForLogin.getUserName(), "Timeout or Error Occurs",coaProfileData.getName(), mvnoId,coaDMResponse);
                        }
                    } catch (Exception ex) {
                        RadiusAsyncUtility radaysn = new RadiusAsyncUtility();
                        radaysn.coaRespnseProcess(coaProfileData.getGateway(),acctReqForLogin.getUserName(), "Error or Timeout",coaProfileData.getName(), mvnoId,coaDMResponse);
                        log.error("CoA/DM Failed:" + ex.getMessage());
                    }
                } else {
                    log.info("Session Not Available for Login COA for FrameIp: " + userIP);
                }
            } else {
                log.error("COA/DM Profile Not Found For Login Session: " + coaDMProfileId);
            }
            responseCode = RadiusConstants.SUCCESS;
            return responseCode;
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error("Error while fetching CoaDMProfiles by id: " + coaDMProfileId + " " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
//            return responseCode;
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    public boolean checkCustomerConcurrency(String userName, String strCalling, Long clientGroupId, CustomerData customerData) {
        boolean isConcurrenyExist = true;
        try {
            DBAccountingDriver dbAccountingDrive = new DBAccountingDriver();
            int currentSession = dbAccountingDrive.getNoofCustomerSessionByCustomerId(clientGroupId, String.valueOf(customerData.getCustid()));
            log.debug("Concurrent Session is: " + currentSession + ": User: " + userName);

            if (customerData.getMaxconcurrentsession() != null && customerData.getMaxconcurrentsession() > 0) {
                if (currentSession >= customerData.getMaxconcurrentsession()) {
                    isConcurrenyExist = false;
                    log.info("Max Concurrent reached for user : " + userName);
                }
            } else if (currentSession >= customerData.getCustomerBasePlan().get(0).getConcurrency()) {
                isConcurrenyExist = false;
                log.info("Max Concurrent reached for user : " + userName);
            }
        } catch (Exception e) {
            log.error("Error while checking concurreny for username : " + userName);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return isConcurrenyExist;
    }

    @Override
    public int generateSNMP(Integer mvnoId, String username, String userIP, boolean isMacProvision, boolean isLogout) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        Integer responseCode = RadiusConstants.FAIL;
        log.debug("In the Generate SNMP");
        try {
            LiveUserSearchDTO paginationDTO = new LiveUserSearchDTO();

            Optional<Customers> customers = customersRepository.findByUsernameAndMvnoId(username, mvnoId);
            //Get detail of Live Users
            paginationDTO.setUserName(username);
            Page<LiveUser> liveUsers = liverUserService.findLiveUsersUsingFilter(paginationDTO, mvnoId);
            List<LiveUser> liveUsersList = new ArrayList<>(liveUsers.getContent());
            if (customers.isPresent()) {
                List<MacAddressMapping> macAddressMappings = macAddressMappingRepository.findMacAddressMappingByCustomerId(Long.valueOf((customers.get().getId())));
                if (!CollectionUtils.isEmpty(macAddressMappings)) {
                    log.debug("Add session with Mac for SNMP: " + macAddressMappings.size());
                    for (MacAddressMapping mapping : macAddressMappings) {
                        log.debug("Add session with Mac for SNMP MAC: " + mapping.getMacAddress());
                        paginationDTO = new LiveUserSearchDTO();
                        paginationDTO.setUserName(mapping.getMacAddress());
                        Page<LiveUser> LiveUsers = liveUserService.findLiveUsersUsingFilter(paginationDTO, null/*mvnoId*/);
                        if (!LiveUsers.isEmpty()) {
                            if (!CollectionUtils.isEmpty(liveUsersList))
                                liveUsersList.addAll(LiveUsers.getContent());
                            else
                                liveUsersList = new ArrayList<>(LiveUsers.getContent());
                        }
                    }
                }
            }
            if (userIP != null && !userIP.isEmpty()) {
                //Get detail of Live Users from framed-ip-address
                paginationDTO = new LiveUserSearchDTO();
                paginationDTO.setFramedIpAddress(userIP);
                Page<LiveUser> liveUsersFromIp = liverUserService.findLiveUsersUsingFilter(paginationDTO, mvnoId);
                if (!liveUsersFromIp.isEmpty()) {
                    List<LiveUser> users = liveUsersFromIp.getContent();
                    if (!CollectionUtils.isEmpty(liveUsersList) && !CollectionUtils.isEmpty(users)) {
                        List<Long> userIds = liveUsersList.stream().map(LiveUser::getCdrID).collect(Collectors.toList());
                        users = users.stream().filter(u -> !userIds.contains(u.getCdrID())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(users))
                            liveUsersList.addAll(users);
                    } else if (!CollectionUtils.isEmpty(users))
                        liveUsersList = users;
                }
            }

            //find by class
            List<LiveUser> liveUsersByClass = liveUserService.findLiveUsersByLClass(username);
            if (!CollectionUtils.isEmpty(liveUsersList) && !CollectionUtils.isEmpty(liveUsersByClass)) {
                List<Long> userIds = liveUsersList.stream().map(LiveUser::getCdrID).collect(Collectors.toList());
                liveUsersByClass = liveUsersByClass.stream().filter(u -> !userIds.contains(u.getCdrID())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(liveUsersByClass))
                    liveUsersList.addAll(liveUsersByClass);
            } else if (!CollectionUtils.isEmpty(liveUsersByClass))
                liveUsersList = liveUsersByClass;

            if (CollectionUtils.isEmpty(liveUsersList)) {
                log.info("No of Live User Found:" + liveUsers.getSize() + ":");
            } else {
                response.put("liveUser", liveUsers);
                for (int j = 0; j < liveUsersList.size(); j++) {
                    LiveUser liveuser = liveUsersList.get(j);
                    List<Client> clientList = clientService.findClientByIpAddress(liveuser.getNasIpAddress(), mvnoId);
                    log.info("NAS IP Found : " + liveuser.getNasIpAddress() + ":UserIP:" + liveuser.getFramedIpAddress() + ":User:" + liveuser.getUserName() + ":");
                    RadiusUtility radiusUtility = new RadiusUtility();
                    AccountingRequest acctReq = (AccountingRequest) radiusUtility.getRequestFromLiveUser(liveuser);
                    for (int i = 0; i < clientList.size(); i++) {
                        Client clientData = clientList.get(i);
                        if (clientData.getClientGroupData() != null) {
                            // skip if data available
                        } else {
                            clientData = clientService.updateRadiusClientData(clientData, acctReq);
                        }
                        ClientGroup cltGroupData = clientData.getClientGroupData();
                        if (cltGroupData != null)
                            log.debug("Client Group Data: " + cltGroupData.getName() + " for live user: " + liveuser.getUserName());
                        //Now Actual CoA Firing Start)
                        if (clientData.getClientGroupData() != null && clientData.isSnmpEnable() && clientData != null) {
                            try {
                                SNMPClientProfile snmpClientProfile = clientData.getSnmpClientProfile();
                                log.info("SNMP Firing on:" + snmpClientProfile.getDestinationIp() + ":Key:" + clientData.getSharedKey() + ":Port:" + snmpClientProfile.getDestinationPort());
                                //Need to work for customer data
                                DBAuthenticationDriver dbAuth = new DBAuthenticationDriver();
                                CustomerData custRetrunData = dbAuth.getDBCustomer(null, customers.get().getMvnoId(), String.valueOf(customers.get().getId()), null, false);
                                try {
                                    log.info("SNMP Firing For User Login Request :" + custRetrunData.toString());
                                    if (!isLogout && custRetrunData.getMacAuthEnable() != null && !custRetrunData.getMacAuthEnable()) {
                                        //save mac in cache
                                        //check faulty mac
                                        // #SUP-1672: ACT Production: When MAC is stored as Faulty MAC, then in that scenario Ericsson IPoE is not working
//                                        CacheRetrival cacheRetrival = new CacheRetrival();
//                                        List<String> faultyMACS = cacheRetrival.getFaultyMacData();
////                                        boolean faultyMacNotFound = true;
//                                        if (!CollectionUtils.isEmpty(faultyMACS)) {
//                                            String normalizedTargetMac = liveuser.getCallingStationId();
//                                            if (normalizedTargetMac != null) {
//                                                normalizedTargetMac = radiusUtility.normalizeMacAddress(liveuser.getCallingStationId());
//                                            }
//                                            boolean isPresent = faultyMACS.stream()
//                                                    .map(radiusUtility::normalizeMacAddress) // Use instance method reference
//                                                    .anyMatch(normalizedTargetMac::equals);
//                                            if (isPresent) {
////                                            if (faultyMACS.contains(liveuser.getCallingStationId())) {
//                                                faultyMacNotFound = false;
//                                                log.error("Faulty Mac Found, So skipp Store Mac: " + liveuser.getCallingStationId());
//                                            }
//                                        }
//                                        if (faultyMacNotFound) {
                                        //save mac in cache
                                        CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                        CustomerDetails customerDetails = new CustomerDetails(custRetrunData.getUsername(), liveuser.getCustid(), liveuser.getlClass());
                                        cacheService.put(radiusUtility.normalizeMacAddress(liveuser.getCallingStationId()), customerDetails);
                                        log.info("SNMP Cache for: " + customerDetails);
//                                        }
                                    } else if (!isLogout && custRetrunData != null && isMacProvision) {
                                        log.info("Save mac in customer from SNMP: " + liveuser.getUserName());
                                        RadiusUtility utility = new RadiusUtility();
//                                        utility.saveMacAndCustomer(true, liveuser.getUserName(), custRetrunData, mvnoId, concurrency, dbAuth, cltGroupData.isLogoutOldSessionOnNew());
                                        utility.saveOrUpdateCustomerMac(liveuser.getUserName(), null, custRetrunData, mvnoId, dbAuth, true);
                                    } else if (isLogout) {
                                        log.info("In Trigger SNMP for logout: " + custRetrunData.getUsername());
                                        CacheServiceWithRedis cacheService = CacheServiceWithRedis.getInstance();
                                        String strCalling = liveuser.getCallingStationId();
                                        if (strCalling != null && cacheService.get(radiusUtility.normalizeMacAddress(strCalling)) != null) {
                                            log.debug("Cache remove on logout for mac: " + strCalling);
                                            cacheService.remove(radiusUtility.normalizeMacAddress(strCalling));
                                        } else {
                                            log.info("No Cache found for Mac:" + strCalling);
                                        }
                                    } else {
                                        log.info("SNMP Mac Provision skip mac Provision: " + isMacProvision + " customer: " + custRetrunData);
                                    }
                                } catch (Exception ex) {
                                    log.debug("Exception to store mac and username in cache: " + ex.getMessage());
                                    ex.printStackTrace();
                                }
//                                    sendSNMP(liveuser.getAcctSessionId(),clientData.getSnmpClientProfile());
                                radiusUtility.sendSNMP(liveuser.getAcctSessionId(), clientData.getSnmpClientProfile());
                            } catch (Exception e) {
                                log.info("SNMP:" + e.getMessage());
                            }
                        } else {
                            log.info("Client Not Found Skipping SNMP");
                        }
                    }
                }
            }

            responseCode = RadiusConstants.SUCCESS;
            return responseCode;
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error("Error in SNNMP" + e.getMessage());
            e.printStackTrace();
            return responseCode;
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }


//    public void sendSNMP(String strSessionid,SNMPClientProfile clientGroup) throws IOException {
//        String targetAddress = clientGroup.getDestinationIp();
//        String port=clientGroup.getDestinationPort();
//        //  String community = "nmp_community";
//        String community = clientGroup.getCommunityString();
//
//        //String oidString = "1.3.6.1.4.1.2352.2.27.1.1.3.9.0"; // sysName
//        String oidString = clientGroup.getBaseOid();
//        Variable oidValue=new Gauge32(Integer.parseInt(clientGroup.getBaseValue()));
//
//        //String oidStringNew = "1.3.6.1.4.1.2352.2.27.1.1.3.4.0";
//        String oidStringNew = clientGroup.getNewOid();
//        String oidValueNew=strSessionid;
//
//        // Create SNMP object
//        Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
//
//        // Set community for SNMPv1/v2c
//        CommunityTarget target = new CommunityTarget();
//        target.setCommunity(new OctetString(community));
//        target.setAddress(new UdpAddress(targetAddress + "/" + port));
//        target.setVersion(1);
//
//        // Create PDU for SET request
//        PDU pdu = new PDU();
//        pdu.setType(PDU.SET);
//
//        // Add OID and new value to PDUs
//        pdu.add(new VariableBinding(new OID(oidString), oidValue));
//        pdu.add(new VariableBinding(new OID(oidStringNew), new OctetString(oidValueNew)));
//
//        // Send SET request
//        snmp.send(pdu, target, null, null);
//
//        System.out.println("Sent SET request for OID: " + oidString + " with new value: " + oidStringNew);
//        log.debug("Sent SET request for OID: " + oidString + " with new value: " + oidStringNew);
//
//        // Close connection
//        snmp.close();
//    }


    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
