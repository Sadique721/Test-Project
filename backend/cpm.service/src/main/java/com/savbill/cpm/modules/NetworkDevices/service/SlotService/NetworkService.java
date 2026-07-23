package com.savbill.cpm.modules.NetworkDevices.service.SlotService;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.NetworkDevices.domain.NetworkDevices;
import com.savbill.cpm.modules.NetworkDevices.mapper.SloatMapper.NetworkMapper;
import com.savbill.cpm.modules.NetworkDevices.model.SloatModel.NetworkDTO;
import com.savbill.cpm.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetworkService extends ExBaseAbstractService<NetworkDTO, NetworkDevices, Long> {
    public NetworkService(NetworkDeviceRepository repository, NetworkMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[Network Service]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Network");
        createExcel(workbook, sheet, NetworkDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, NetworkDTO.class, null);
    }
}
