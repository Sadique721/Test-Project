package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.mapper.postpaid.DiscountPlanMappingMapper;
import com.savbill.cpm.model.postpaid.DiscountMapping;
import com.savbill.cpm.pojo.api.DiscountMappingPojo;
import com.savbill.cpm.repository.postpaid.DiscountMappingRepository;
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
public class DiscountPlanMappingService extends AbstractService<DiscountMapping, DiscountMappingPojo, Integer> {

    @Autowired
    private DiscountMappingRepository entityRepository;
    @Autowired
    private DiscountPlanMappingMapper mapper;

    @Override
    protected JpaRepository<DiscountMapping, Integer> getRepository() {
        return entityRepository;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Discount Plan Mapping");
        List<DiscountMappingPojo> discountMappingPojoList = entityRepository.findAll().stream()
                .map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, DiscountMappingPojo.class, discountMappingPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<DiscountMappingPojo> discountMappingPojoList = entityRepository.findAll().stream()
                .map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, DiscountMappingPojo.class, discountMappingPojoList, null);
    }
}
