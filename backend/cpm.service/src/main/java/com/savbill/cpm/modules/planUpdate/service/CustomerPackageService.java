package com.savbill.cpm.modules.planUpdate.service;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.planUpdate.domain.CustomerPackage;
import com.savbill.cpm.modules.planUpdate.mapper.CustomerPackageMapper;
import com.savbill.cpm.modules.planUpdate.model.CustomerPackageDTO;
import com.savbill.cpm.modules.planUpdate.repository.CustomerPackageRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerPackageService extends ExBaseAbstractService<CustomerPackageDTO, CustomerPackage, Long> {

    @Autowired
    private CustomerPackageRepository customerPackageRepository;
    @Autowired
    private CustomerPackageMapper customerPackageMapper;

    public CustomerPackageService(CustomerPackageRepository repository, CustomerPackageMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "CustomerPackageService";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }

    public List<CustomerPackageDTO> findAllByCustomersId(Integer id){
        return customerPackageRepository.findAllByCustomersId(id)
                .stream().map(domain -> customerPackageMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("CustomerPackage");
        createExcel(workbook, sheet, CustomerPackageDTO.class, null);
    }
    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, CustomerPackageDTO.class, null);
    }

    public CustomerPackageDTO findParentCustPackageDetailByExpiryDate(Integer id){
        List<CustomerPackageDTO> list=customerPackageRepository.findParentCustPackageDetailByExpiryDate(id, LocalDateTime.now())
                .stream().map(domain -> customerPackageMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
        return list.get(0);
    }

    public List<CustomerPackage> getAllByCustomer(Integer customerid) {
        return customerPackageRepository.findAllByCustomersId(customerid);
    }
}
