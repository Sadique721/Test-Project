package com.savbill.ticketmanagement.core.modules.NetworkDevices.service;


import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.GenericSearchModel;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.NetworkDevices.domain.NetworkDevices;
import com.savbill.ticketmanagement.core.modules.NetworkDevices.dto.NetworkDTO;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetworkService extends ExBaseAbstractService<NetworkDTO, NetworkDevices, Long> {


    public NetworkService(JpaRepository<NetworkDevices, Long> repository, IBaseMapper<NetworkDTO, NetworkDevices> mapper) {
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
