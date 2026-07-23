package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.mapper.postpaid.TaxTypeSlabMapper;
import com.savbill.cpm.model.postpaid.TaxTypeSlab;
import com.savbill.cpm.pojo.api.TaxTypeSlabPojo;
import com.savbill.cpm.repository.postpaid.TaxTypeSlabRepository;
import com.savbill.cpm.service.radius.AbstractService;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public Page<TaxTypeSlab> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest);
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Charges");
        List<TaxTypeSlabPojo> taxTypeSlabPojos = entityRepository.findAll().stream()
                .map(data -> taxTypeSlabMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, TaxTypeSlabPojo.class, taxTypeSlabPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<TaxTypeSlabPojo> taxTypeSlabPojos = entityRepository.findAll().stream()
                .map(data -> taxTypeSlabMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, TaxTypeSlabPojo.class, taxTypeSlabPojos, null);
    }
}
