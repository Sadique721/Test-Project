package com.savbill.radius.services.impl;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.Customer;
import com.savbill.radius.entity.Customers;
import com.savbill.radius.entity.MacAddressMapping;
import com.savbill.radius.entity.MacAddressMappingDto;
import com.savbill.radius.kafka.KafkaMessageData;
import com.savbill.radius.kafka.KafkaMessageSender;
import com.savbill.radius.kafka.message.MacAddressMappingMessage;
import com.savbill.radius.repository.CustomerRepository;
import com.savbill.radius.repository.CustomersRepository;
import com.savbill.radius.repository.MacAddressMappingRepository;
import com.savbill.radius.services.MacAddressMappingService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MacAddressMappingServiceImpl implements MacAddressMappingService {
    private static final Logger log = LoggerFactory.getLogger(MacAddressMappingServiceImpl.class);
    @Autowired
    private MacAddressMappingRepository macAddressMappingRepository;

//    @Autowired
//    private MessageSender messageSender;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<MacAddressMapping> findMacAddressMappingByCustomerId(Long customerId) {
        try {
            validateMacAddressMappingByCustomerId(customerId);
            QMacAddressMapping qMacAddressMapping = QMacAddressMapping.macAddressMapping;
            BooleanExpression boolExp = qMacAddressMapping.isNotNull();
            boolExp = boolExp.and(qMacAddressMapping.customerId.eq(customerId));
            return (List<MacAddressMapping>) macAddressMappingRepository.findAll(boolExp);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateMacAddressMappingByCustomerId(Long customerId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(customerId)) {
                throw new IllegalArgumentException("Please enter valid Customer id.");
            }
            QCustomer qCustomer = QCustomer.customer;
            BooleanExpression boolExp = qCustomer.isNotNull();
            boolExp = boolExp.and(qCustomer.customerId.eq(customerId));
            Optional<Customer> customerOptional = customerRepository.findOne(boolExp);
            if (!customerOptional.isPresent())
                throw new IllegalArgumentException("No record found for customer with id : '" + customerId + "'. Please enter valid customer id.");
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<MacAddressMapping> findAllMacAddressMapping() {
        try {
            return macAddressMappingRepository.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteMacAddressMappingById(Long id) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            validateMacAddressMappingById(id);
            macAddressMappingRepository.deleteById(id);
            //log.info("MacAddress Mapping deleted successfully: "+id);
        } catch (RuntimeException e) {
            //	log.error("Error to delete MacAddress mapping: "+e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    private Optional<MacAddressMapping> validateMacAddressMappingById(Long id) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter valid SMS Config Mapping id.");
            Optional<MacAddressMapping> coaProfileAttribute = macAddressMappingRepository.findById(id);

            if (!coaProfileAttribute.isPresent()) {
                throw new IllegalArgumentException("No record found for Mac Address Mapping with id : '" + id + "'. Please enter valid Mac Address Mapping id.");
            }

            return coaProfileAttribute;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    private Optional<MacAddressMapping> validateMacAddressMappingById(Long id, Integer mvnoId) {
//        try {
//            if(!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
//                throw new IllegalArgumentException("Please enter valid SMS Config Mapping id.");
//
//            QMacAddressMapping qMacAddressMapping = QMacAddressMapping.macAddressMapping;
//            BooleanExpression boolExp = qMacAddressMapping.isNotNull();
//            boolExp = boolExp.and(qMacAddressMapping.macAddressId.eq(id));
//            if(mvnoId != 1)
//                boolExp = boolExp.and(qMacAddressMapping.mvnoId.eq(mvnoId));
//
//            Optional<MacAddressMapping> coaProfileAttribute = macAddressMappingRepository.findOne(boolExp);
//
//            if(!coaProfileAttribute.isPresent()) {
//                throw new IllegalArgumentException("You are not authorised to update/delete this record.");
//            }
//
//            return coaProfileAttribute;
//        }
//        catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    @Override
    public MacAddressMapping saveMacAddressMapping(MacAddressMappingDto macAddressMappingDto) {
        try {
            MacAddressMapping macAddressMapping = new MacAddressMapping(macAddressMappingDto);
            validateMacAddressMappingByCustomerId(macAddressMappingDto.getCustomerId());
            validateMacAddressMappingData(macAddressMapping, false);
            macAddressMapping.setCreateDate(new Timestamp(new Date().getTime()));
            macAddressMapping.setLastModificationDate(new Timestamp(new Date().getTime()));
            macAddressMapping.setNormalizeMac(normalizeMacAddress(macAddressMapping.getMacAddress()));
            return macAddressMappingRepository.save(macAddressMapping);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateMacAddressMappingData(MacAddressMapping macAddressMapping, boolean isUpdate) {
        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(macAddressMapping.getMacAddress())) {
            throw new IllegalArgumentException("Mac Address is mandatory. Please enter valid Mac Address.");
        } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(macAddressMapping.getCustomerId())) {
            throw new IllegalArgumentException("Customer Id is mandatory. Please enter valid Customer Id.");
        }
    }

    @Override
    public List<MacAddressMapping> updateMacAddressMapping(List<MacAddressMapping> macAddressMappingList) {
        try {
            List<MacAddressMapping> updateMacAddressMappingList = getChangedMacAddressMapping(findAllMacAddressMapping(), macAddressMappingList);
            for (MacAddressMapping MacAddressMapping : updateMacAddressMappingList) {
                Optional<MacAddressMapping> oldMacAddressMapping = validateMacAddressMappingById(MacAddressMapping.getMacAddressId());
                validateMacAddressMappingData(MacAddressMapping, true);
                MacAddressMapping.setCreateDate(oldMacAddressMapping.get().getCreateDate());
                MacAddressMapping.setLastModificationDate(new Timestamp(new Date().getTime()));
                MacAddressMapping.setNormalizeMac(normalizeMacAddress(MacAddressMapping.getMacAddress()));
            }
            macAddressMappingRepository.saveAll(updateMacAddressMappingList);
            return macAddressMappingRepository.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<MacAddressMapping> getChangedMacAddressMapping(List<MacAddressMapping> dbMacAddressMappingList, List<MacAddressMapping> MacAddressMappingList) {
        List<MacAddressMapping> macAddressMappings = MacAddressMappingList.stream()
                .filter(macAddressMapping -> dbMacAddressMappingList.stream()
                        .noneMatch(dbMacAddressMapping -> dbMacAddressMapping.getMacAddress().equals(macAddressMapping.getMacAddress()))).collect(Collectors.toList());
        List<MacAddressMapping> saveAttributes = macAddressMappings.stream()
                .filter(macAddressMapping -> MacAddressMappingList.stream()
                        .noneMatch(dbMacAddressMapping -> macAddressMapping.getCustomerId() == null ? false : macAddressMapping.getCustomerId().equals(dbMacAddressMapping.getCustomerId())
                                && macAddressMapping.getMacAddressId() == null ? false : macAddressMapping.getMacAddressId().equals(dbMacAddressMapping.getMacAddressId())
                        ))
                .collect(Collectors.toList());
        for (MacAddressMapping macAddressMapping : saveAttributes) {
            macAddressMapping.setCreateDate(new Timestamp(new Date().getTime()));
            macAddressMapping.setLastModificationDate(new Timestamp(new Date().getTime()));
            macAddressMapping.setCustomerId(MacAddressMappingList.get(0).getCustomerId());
            validateMacAddressMappingData(macAddressMapping, false);
            macAddressMapping.setNormalizeMac(normalizeMacAddress(macAddressMapping.getMacAddress()));
            macAddressMappingRepository.save(macAddressMapping);
        }
        macAddressMappings.removeAll(saveAttributes);
        return macAddressMappings;
    }

    @Override
    public List<HashMap<String, Object>> findMacAddressMappingByUserName(String userName, int mvnoId) {
        try {
            List<Customers> customers = validateMacAddressMappingByCustomerUserName(userName, mvnoId);
            List<MacAddressMapping> list = new ArrayList<>();
            for (Customers customer : customers) {
                QMacAddressMapping qMacAddressMapping = QMacAddressMapping.macAddressMapping;
                BooleanExpression boolExp = qMacAddressMapping.isNotNull();
                boolExp = boolExp.and(qMacAddressMapping.customerId.eq(customer.getId().longValue()));
                list.addAll((List<MacAddressMapping>) macAddressMappingRepository.findAll(boolExp));
            }
            return convertMacAddressToList(list, customers);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<Customers> validateMacAddressMappingByCustomerUserName(String userName, int mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(userName)) {
                throw new IllegalArgumentException("Please enter valid Customer username.");
            }
            QCustomers qCustomers = QCustomers.customers;
            BooleanExpression boolExp = qCustomers.isNotNull();
            boolExp = boolExp.and(qCustomers.username.like("%" + userName + "%"));
            if (mvnoId != 1) {
                boolExp = boolExp.and(qCustomers.mvnoId.eq(mvnoId));
            }
            List<Customers> customers = (List<Customers>) customersRepository.findAll(boolExp);
            if (CollectionUtils.isEmpty(customers))
                throw new IllegalArgumentException("No record found for customer with username : '" + userName + "'. Please enter valid customer id.");
            return customers;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public List<HashMap<String, Object>> convertMacAddressToList(List<MacAddressMapping> list, List<Customers> customers) {

        List<HashMap<String, Object>> maps = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(l -> {
                HashMap<String, Object> response = new HashMap<>();
                Optional<Customers> customer = customers.stream().filter(c -> c.getId().equals(l.getCustomerId().intValue())).findFirst();
                if (customer.isPresent()) {
                    response.put("id", l.getMacAddressId());
                    response.put("userName", customer.get().getUsername());
                    response.put("mode", customer.get().getTitle()); /**Just for placeholder, Plan type is pass @Author Dhaval Khalasi**/
                    response.put("macAddress", l.getMacAddress());
                    response.put("loginDate", customer.get().getCreatedate());
                    response.put("mvnoId", customer.get().getMvnoId());
                    response.put("expiryDate", customer.get().getCreatedate());
                    response.put("customerStatus", customer.get().getStatus());
                    maps.add(response);
                }
            });
        }
        return maps;
    }

    @Transactional
    @Override
    public String deleteMacAddressByUserNameAndMac(Set<Long> macs) {

        List<MacAddressMapping> macList = macAddressMappingRepository.findByMacAddressIdIn(macs.stream().collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(macList)) {
            return "No mapping found for given id(s)";
//            throw new IllegalArgumentException("Mac address not available for given ids: ");
        }
        Set<Long> customerIds = macList.stream().map(MacAddressMapping::getCustomerId).collect(Collectors.toSet());
        List<Customers> customers = customersRepository.findByIdIn(customerIds.stream().map(aLong -> aLong.intValue()).collect(Collectors.toList()));
        macAddressMappingRepository.deleteBymacAddressId(macs.stream().collect(Collectors.toList()));
        List<HashMap<String, Object>> response = convertMacAddressToList(macList, customers);
        MacAddressMappingMessage message = new MacAddressMappingMessage(response, false, true);
        //messageSender.send(message, RabbitMqConstants.QUEUE_DELETE_MAC_FROM_RADIUS);
        kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
        return "Mac addresses deleted successfully!";
    }

    public String normalizeMacAddress(String macAddress) {
        if (macAddress != null)
            return macAddress.replace(":", "").replace("-", "").replace(".", "");
        return macAddress;
    }
}
