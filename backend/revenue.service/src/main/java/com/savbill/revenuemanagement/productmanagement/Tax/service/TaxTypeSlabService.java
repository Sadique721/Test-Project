package com.savbill.revenuemanagement.productmanagement.Tax.service;

import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeSlab;
import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxTypeSlabPojo;
import com.savbill.revenuemanagement.productmanagement.Tax.mapper.TaxTypeSlabMapper;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxTypeSlabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TaxTypeSlabService extends AbstractService<TaxTypeSlab, TaxTypeSlabPojo, Integer> {

    @Autowired
    private TaxTypeSlabRepository entityRepository;
    @Autowired
    private TaxTypeSlabMapper taxTypeSlabMapper;

    @Override
    protected JpaRepository<TaxTypeSlab, Integer> getRepository() {
        return entityRepository;
    }
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
