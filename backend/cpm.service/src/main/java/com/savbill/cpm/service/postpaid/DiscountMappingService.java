package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.mapper.postpaid.DiscountMappingMapper;
import com.savbill.cpm.model.postpaid.DiscountPlanMapping;
import com.savbill.cpm.pojo.api.DiscountPlanMappingPojo;
import com.savbill.cpm.repository.postpaid.DiscountPlanMappingRepo;
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
public class DiscountMappingService extends AbstractService<DiscountPlanMapping, DiscountPlanMappingPojo, Integer> {

    @Autowired
    private DiscountPlanMappingRepo entityRepository;
    @Autowired
    private DiscountMappingMapper discountMappingMapper;

    @Override
    protected JpaRepository<DiscountPlanMapping, Integer> getRepository() {
        return entityRepository;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Discount Mapping");
        List<DiscountPlanMappingPojo> discountPlanMappingPojoList = entityRepository.findAll().stream()
                .map(data -> discountMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, DiscountPlanMappingPojo.class, discountPlanMappingPojoList, null);
    }
    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<DiscountPlanMappingPojo> discountPlanMappingPojoList = entityRepository.findAll().stream()
                .map(data -> discountMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, DiscountPlanMappingPojo.class, discountPlanMappingPojoList, null);
    }
}
