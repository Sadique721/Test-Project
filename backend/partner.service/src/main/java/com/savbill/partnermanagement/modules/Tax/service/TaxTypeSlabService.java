package com.savbill.partnermanagement.modules.Tax.service;

import com.savbill.partnermanagement.modules.Tax.mapper.TaxTypeSlabMapper;
import com.savbill.partnermanagement.modules.Tax.repository.TaxTypeSlabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxTypeSlabService {

    @Autowired
    private TaxTypeSlabRepository entityRepository;
    @Autowired
    private TaxTypeSlabMapper taxTypeSlabMapper;


//
//    public Page<TaxTypeSlab> searchEntity(String searchText, Integer pageNumber, int pageSize) {
//        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
//        return entityRepository.searchEntity(searchText, pageRequest);
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("Charges");
//        List<TaxTypeSlabPojo> taxTypeSlabPojos = entityRepository.findAll().stream()
//                .map(data -> taxTypeSlabMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        createExcel(workbook, sheet, TaxTypeSlabPojo.class, taxTypeSlabPojos, null);
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        List<TaxTypeSlabPojo> taxTypeSlabPojos = entityRepository.findAll().stream()
//                .map(data -> taxTypeSlabMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        createPDF(doc, TaxTypeSlabPojo.class, taxTypeSlabPojos, null);
//    }
}
