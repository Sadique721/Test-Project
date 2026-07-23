package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.mapper.postpaid.EndMacMapper;
import com.savbill.cpm.model.postpaid.EndMacMappping;
import com.savbill.cpm.model.postpaid.EndMacMapppingPojo;
import com.savbill.cpm.model.postpaid.QEndMacMappping;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.repository.postpaid.EndMacMapppingRepository;
import com.savbill.cpm.service.radius.AbstractService;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EndMacMapppingService extends AbstractService<EndMacMappping, EndMacMapppingPojo, Integer> {

    @Autowired
    private EndMacMapppingRepository endMacMapppingRepository;
    @Autowired
    private EndMacMapper endMacMapper;
    @Autowired
    private MessageSender messageSender;

//    @Autowired
//    private CustomersRepository custRepository;

    @Override
    protected JpaRepository<EndMacMappping, Integer> getRepository() {
        return endMacMapppingRepository;
    }

    public void saveAll(List<EndMacMappping> endMacMapppingList) {
        endMacMapppingRepository.saveAll(endMacMapppingList);
    }

    public List<EndMacMappping> findMacAddressByOwnerIdAndOwnerType(Integer ownerId, String ownerType) {
        return endMacMapppingRepository.findByOwnerIdAndOwnerTypeAndIsDeletedIsFalse(ownerId, ownerType);
    }

    public void deleteByCustomerId(Integer ownerId, String ownerType) {
        List<EndMacMappping> endMacMapppingList = endMacMapppingRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
        for (EndMacMappping endMacMappping : endMacMapppingList) {
            endMacMappping.setIsDeleted(true);
            endMacMapppingRepository.save(endMacMappping);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Mac");
        List<EndMacMapppingPojo> endMacMapppingPojoList = endMacMapppingRepository.findAll().stream()
                .map(data -> endMacMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        Field[] printFields = {
                EndMacMapppingPojo.class.getDeclaredField("id"),
                EndMacMapppingPojo.class.getDeclaredField("custid"),
                EndMacMapppingPojo.class.getDeclaredField("macAddress"),
                EndMacMapppingPojo.class.getDeclaredField("isDeleted")
        };
        createExcel(workbook, sheet, EndMacMapppingPojo.class, endMacMapppingPojoList, printFields);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<EndMacMapppingPojo> endMacMapppingPojoList = endMacMapppingRepository.findAll().stream()
                .map(data -> endMacMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        Field[] printFields = {
                EndMacMapppingPojo.class.getDeclaredField("id"),
                EndMacMapppingPojo.class.getDeclaredField("custid"),
                EndMacMapppingPojo.class.getDeclaredField("macAddress"),
                EndMacMapppingPojo.class.getDeclaredField("isDeleted")
        };
        createPDF(doc, EndMacMapppingPojo.class, endMacMapppingPojoList, null);
    }

    @Override
    public EndMacMappping save(EndMacMappping entity) {
        EndMacMappping endMacMappping = super.save(entity);
        return endMacMappping;
    }

    @Override
    public EndMacMappping update(EndMacMappping entity) {
        EndMacMappping endMacMappping = super.update(entity);
        return endMacMappping;
    }

    @Transactional
    public void deleteByMacAddress(String macAddress, Integer ownerId, String ownerType) {
        QEndMacMappping qEndMacMappping = QEndMacMappping.endMacMappping;
//        Customers customer = custRepository.findByIdAndIsDeletedIsFalse(customerId);
        List<EndMacMappping> customerMacMapping = (List<EndMacMappping>) endMacMapppingRepository.findAll(qEndMacMappping.macAddress.eq(macAddress).and(qEndMacMappping.ownerId.eq(ownerId)).and(qEndMacMappping.ownerType.eq(ownerType)));
//        List<String> macAddresses = customerMacMapping.stream().map(entry -> entry.getMacAddress()).collect(Collectors.toList());
//        customerMacMapping.forEach(s -> {
//            s.setIsDeleted(true);
//            CustMacMappingMessage message = new CustMacMappingMessage(s, customer.getMvnoId(), customer.getUsername());
//            messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
//
//        });
        endMacMapppingRepository.deleteInBatch(customerMacMapping);
    }

}
