package com.savbill.cpm.modules.servicePlan.service;

import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.servicePlan.domain.Services;
import com.savbill.cpm.modules.servicePlan.mapper.ServicesMapper;
import com.savbill.cpm.modules.servicePlan.model.ServicesDTO;
import com.savbill.cpm.modules.servicePlan.repository.ServiceRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

@Service
public class ServicesService extends ExBaseAbstractService<ServicesDTO, Services, Long> {



    public ServicesService(ServiceRepository repository, ServicesMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ServicesService]";
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Services");
        createExcel(workbook, sheet, ServicesDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, ServicesDTO.class, null);
    }
}
