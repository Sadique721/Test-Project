package com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping;

import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.rabbitmq.CustMacMappingMessage;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustMacMappingService extends ExBaseAbstractService<CustMacMapppingPojo, CustMacMappping, Integer> {

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;

    public CustMacMappingService(CustMacMapppingRepository repository, CustMacMappingMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public CustMacMappping save(CustMacMappping entity) {
        try {
            Long custServicemappingId = entity.getCustServicemappingId();
            CustMacMappping custMacMappping = super.save(entity);
            Customers customer = customersRepository.findByIdAndIsDeletedIsFalse(entity.getCustomer().getId());
            custMacMappping.setCustServicemappingId(custServicemappingId);
            CustMacMappingMessage message = new CustMacMappingMessage(custMacMappping, customer.getMvnoId(), customer.getUsername());
            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
            return custMacMappping;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustMacMappping update(CustMacMappping entity) {
        try {
            CustMacMappping custMacMappping = super.update(entity);
            Customers customer = customersRepository.findByIdAndIsDeletedIsFalse(entity.getCustomer().getId());
            CustMacMappingMessage message = new CustMacMappingMessage(custMacMappping, customer.getMvnoId(), customer.getUsername());
            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
            return custMacMappping;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

//    @Transactional
//    public void deleteByMacAddress(String macAddress, Integer customerId) {
//        QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
//        Customers customer = customersRepository.findByIdAndIsDeletedIsFalse(customerId);
//        BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
//        booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.eq(macAddress)).and(qCustMacMappping.customer.id.eq(customerId));
//        List<CustMacMappping> customerMacMapping = (List<CustMacMappping>) custMacMapppingRepository.findAll(booleanExpression);

    ////        List<String> macAddresses = customerMacMapping.stream().map(entry -> entry.getMacAddress()).collect(Collectors.toList());
//        customerMacMapping.forEach(s -> {
//            s.setIsDeleted(true);
//            CustMacMappingMessage message = new CustMacMappingMessage(s, customer.getMvnoId(), customer.getUsername());
//            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
//            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
//            custMacMapppingRepository.save(s);
//        });
//    }
    @Transactional
    public void deleteByMacAddress(String macAddress, Customers customer) {
        try {
            QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
            BooleanExpression booleanExpression = qCustMacMappping.isNotNull()
                    .and(qCustMacMappping.macAddress.eq(macAddress))
                    .and(qCustMacMappping.customer.id.eq(customer.getId()));
            List<CustMacMappping> customerMacMappings = (List<CustMacMappping>) custMacMapppingRepository.findAll(booleanExpression);

            if (customerMacMappings.isEmpty()) {
                return;
            }
            List<KafkaMessageData> kafkaMessages = new ArrayList<>();
            customerMacMappings.forEach(mapping -> {
                mapping.setIsDeleted(true);
                CustMacMappingMessage message = new CustMacMappingMessage(mapping, customer.getMvnoId(), customer.getUsername());
                kafkaMessages.add(new KafkaMessageData(message, message.getClass().getSimpleName()));
            });
            custMacMapppingRepository.saveAll(customerMacMappings);
            kafkaMessages.forEach(kafkaMessageSender::send);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public CustMacMappping saveData(CustMacMappping entity, Customers customers) {
        try {
            CustMacMappping custMacMappping = super.save(entity);
            custMacMappping.setCustServicemappingId(entity.getCustServicemappingId());
            CustMacMappingMessage message = new CustMacMappingMessage(custMacMappping, customers.getMvnoId(), customers.getUsername());
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
            return custMacMappping;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

