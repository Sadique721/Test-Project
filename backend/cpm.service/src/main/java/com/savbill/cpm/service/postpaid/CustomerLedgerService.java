package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.mapper.postpaid.CustomerLedgerMapper;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.postpaid.CustomerLedger;
import com.savbill.cpm.model.postpaid.CustomerLedgerPojo;
import com.savbill.cpm.repository.postpaid.CustomerLedgerRepository;
import com.savbill.cpm.service.radius.AbstractService;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerLedgerService extends AbstractService<CustomerLedger, CustomerLedgerPojo, Integer> {

    @Autowired
    private CustomerLedgerRepository entityRepository;

    @Autowired
    private CustomerLedgerMapper customerLedgerMapper;

    @Override
    protected JpaRepository<CustomerLedger, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<CustomerLedger> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}

//    public List<CustomerLedger>getAllActiveEntities(){
//    	return entityRepository.findByStatus("Y");
//    }

    public List<CustomerLedger> getAllEntities(Integer pageNumber, int pageSize) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findAll();
    }

    public List<CustomerLedger> getCustomerLeger(Customers customer) {
        return entityRepository.findByCustomer(customer);
    }

    public CustomerLedger getCustomerLeger(Integer custId) {
        return entityRepository.findByCustomerId(custId);
    }



    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Customer Ledger");
        List<CustomerLedgerPojo> customerLedgerPojoList = entityRepository.findAll().stream()
                .map(data -> customerLedgerMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, CustomerLedgerPojo.class, customerLedgerPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<CustomerLedgerPojo> customerLedgerPojoList = entityRepository.findAll().stream()
                .map(data -> customerLedgerMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, CustomerLedgerPojo.class, customerLedgerPojoList, null);
    }

}
