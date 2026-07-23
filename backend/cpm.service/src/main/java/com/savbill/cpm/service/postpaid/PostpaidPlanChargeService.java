package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.constants.cacheKeys;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.mapper.postpaid.PostpaidPlanChargeMapper;
import com.savbill.cpm.model.postpaid.PostpaidPlanCharge;
import com.savbill.cpm.pojo.api.PostpaidPlanChargePojo;
import com.savbill.cpm.repository.postpaid.PostpaidPlanChargeRepo;
import com.savbill.cpm.service.CacheService;
import com.savbill.cpm.service.radius.AbstractService;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostpaidPlanChargeService extends AbstractService<PostpaidPlanCharge, PostpaidPlanChargePojo, Integer> {

    @Autowired
    private PostpaidPlanChargeRepo entityRepository;
    @Autowired
    private PostpaidPlanChargeMapper postpaidPlanChargeMapper;
    @Autowired
    private CacheService cacheService;

    @Override
    protected JpaRepository<PostpaidPlanCharge, Integer> getRepository() {
        return entityRepository;
    }

    public Page<PostpaidPlanCharge> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest);
    }

    public List<PostpaidPlanCharge> getPostpaidPlanChargesByPlanId(Integer planId) {
        return entityRepository.findAllByPlan(planId);
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Charges");
        List<PostpaidPlanChargePojo> postpaidPlanChargePojos = entityRepository.findAll().stream()
                .map(data -> postpaidPlanChargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, PostpaidPlanChargePojo.class, postpaidPlanChargePojos, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<PostpaidPlanChargePojo> postpaidPlanChargePojos = entityRepository.findAll().stream()
                .map(data -> postpaidPlanChargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, PostpaidPlanChargePojo.class, postpaidPlanChargePojos, null);
    }


    public List<Double> getChargeListByChargeIdAndPlanId(Integer planId, Integer chargeId) {
        String cacheKey = cacheKeys.CHARGE_LIST + planId + "_" + chargeId;

        try {
            List<Double> cachedCharges = (List<Double>) cacheService.getFromCache(cacheKey, List.class);
            if (cachedCharges != null) {
                return cachedCharges;
            }

            List<Double> chargeList = entityRepository.getChargeListByChargeIdAndPlanId(planId, chargeId);
            if (!chargeList.isEmpty()) {
                cacheService.putInCache(cacheKey, chargeList);
            }
            return chargeList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
