package com.savbill.cpm.service.radius;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.mapper.postpaid.PlanMapper;
import com.savbill.cpm.model.radius.Plan;
import com.savbill.cpm.pojo.api.PlanPojo;
import com.savbill.cpm.repository.radius.PlanRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class PlanService extends AbstractService<Plan, PlanPojo, Integer> {
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private PlanMapper planMapper;

    @Override
    protected JpaRepository<Plan, Integer> getRepository() {
        return planRepository;
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.radius.Plan', '1')")
    public Page<Plan> findPlan(String trim, Integer pageNumber, int dbPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, dbPageSize);
        return planRepository.findPlans(trim, pageRequest);
    }

    public Plan findById(Integer id) {
        return planRepository.findById(id).orElse(null);
    }

    public TreeMap<Integer, String> getAllPlan() {
        TreeMap<Integer, String> list = new TreeMap<>();
        for (Plan plan : planRepository.findAll()) {
            list.put(plan.getId(), plan.getName());
        }
        return list;
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.radius.Plan', '1')")
    public Page<Plan> getList(Integer pageNumber, int customPageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, customPageSize);
        return planRepository.findAll(pageRequest);
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.radius.Plan', '2')")
    public Plan savePlan(Plan plan) {
        Plan save = planRepository.save(plan);
        return save;
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.radius.Plan', '4')")
    public void deletePlan(Integer id) {
        planRepository.deleteById(id);
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.radius.Plan', '2')")
    public Plan getPlanForAdd() {
        return new Plan();
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.radius.Plan', '2')")
    public Plan getPlanForEdit(Integer id) {
        return planRepository.getOne(id);
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Plan");
        List<PlanPojo> planPojoList = planRepository.findAll().stream()
                .map(data -> planMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, PlanPojo.class, planPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<PlanPojo> planPojoList = planRepository.findAll().stream()
                .map(data -> planMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, PlanPojo.class, planPojoList, null);
    }
}
