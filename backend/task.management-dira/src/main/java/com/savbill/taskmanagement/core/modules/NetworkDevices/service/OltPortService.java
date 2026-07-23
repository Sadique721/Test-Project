package com.savbill.taskmanagement.core.modules.NetworkDevices.service;


import com.savbill.taskmanagement.core.modules.NetworkDevices.domain.OLTPortDetails;
import com.savbill.taskmanagement.core.modules.NetworkDevices.dto.NetworkDTO;
import com.savbill.taskmanagement.core.modules.NetworkDevices.dto.OLTPortDTO;
import com.savbill.taskmanagement.core.modules.NetworkDevices.mapper.OltPortMapper;
import com.savbill.taskmanagement.core.modules.NetworkDevices.repository.OltPortRepository;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

@Service
public class OltPortService extends ExBaseAbstractService<OLTPortDTO, OLTPortDetails, Long> {

    public OltPortService(OltPortRepository repository, OltPortMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[OltPortService]";
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("OltPort");
        createExcel(workbook, sheet, OLTPortDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, NetworkDTO.class, null);
    }

//    @Override
//    public GenericDataDTO search(GenericSearchDTO filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        return null;
//    }
}
