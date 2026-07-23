package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.mapper.postpaid.TaxTypeTierMapper;
import com.savbill.cpm.model.postpaid.TaxTypeTier;
import com.savbill.cpm.pojo.api.TaxTypeTierPojo;
import com.savbill.cpm.repository.postpaid.TaxTypeTierRepository;
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
public class TaxTypeTierService extends AbstractService<TaxTypeTier, TaxTypeTierPojo, Integer> {

    @Autowired
    private TaxTypeTierRepository entityRepository;
    @Autowired
    private TaxTypeTierMapper taxTypeTierMapper;

    @Override
    protected JpaRepository<TaxTypeTier, Integer> getRepository() {
        return entityRepository;
    }

    public Page<TaxTypeTier> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest);
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Charges");
        List<TaxTypeTierPojo> taxTypeTierPojos = entityRepository.findAll().stream()
                .map(data -> taxTypeTierMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, TaxTypeTierPojo.class, taxTypeTierPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<TaxTypeTierPojo> taxTypeTierPojos = entityRepository.findAll().stream()
                .map(data -> taxTypeTierMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, TaxTypeTierPojo.class, taxTypeTierPojos, null);
    }
}
