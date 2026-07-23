package com.savbill.revenuemanagement.productmanagement.Discount.service;


import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountPlanMapping;
import com.savbill.revenuemanagement.productmanagement.Discount.dto.DiscountPlanMappingPojo;
import com.savbill.revenuemanagement.productmanagement.Discount.mapper.DiscountMappingMapper;
import com.savbill.revenuemanagement.productmanagement.Discount.repocitory.DiscountPlanMappingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

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
//
//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("Discount Mapping");
//        List<DiscountPlanMappingPojo> discountPlanMappingPojoList = entityRepository.findAll().stream()
//                .map(data -> discountMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        createExcel(workbook, sheet, DiscountPlanMappingPojo.class, discountPlanMappingPojoList, null);
//    }
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        List<DiscountPlanMappingPojo> discountPlanMappingPojoList = entityRepository.findAll().stream()
//                .map(data -> discountMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        createPDF(doc, DiscountPlanMappingPojo.class, discountPlanMappingPojoList, null);
//    }
}
