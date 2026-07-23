package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.service.SlotService;

import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.OLTPortDetails;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.mapper.SloatMapper.OltPortMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.SloatModel.OLTPortDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.OltPortRepository;
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

//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("OltPort");
//        createExcel(workbook, sheet, OLTPortDTO.class, null);
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        createPDF(doc, NetworkDTO.class, null);
//    }

//    @Override
//    public GenericDataDTO search(GenericSearchDTO filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        return null;
//    }
}
