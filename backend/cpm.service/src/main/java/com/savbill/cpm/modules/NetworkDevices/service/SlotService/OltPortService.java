package com.savbill.cpm.modules.NetworkDevices.service.SlotService;

import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.NetworkDevices.domain.OLTPortDetails;
import com.savbill.cpm.modules.NetworkDevices.mapper.SloatMapper.OltPortMapper;
import com.savbill.cpm.modules.NetworkDevices.model.SloatModel.NetworkDTO;
import com.savbill.cpm.modules.NetworkDevices.model.SloatModel.OLTPortDTO;
import com.savbill.cpm.modules.NetworkDevices.repository.OltPortRepository;
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
