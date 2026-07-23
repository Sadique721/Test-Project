package com.savbill.ticketmanagement.core.modules.tickets.service;


import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.GenericSearchModel;
import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseAssignment;
import com.savbill.ticketmanagement.core.modules.tickets.mapper.CaseAssignmentMapper;
import com.savbill.ticketmanagement.core.modules.tickets.model.CaseAssignmentDTO;
import com.savbill.ticketmanagement.core.modules.tickets.repository.CaseAssignmentRepository;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseAssignmentService extends ExBaseAbstractService<CaseAssignmentDTO, CaseAssignment, Long> {

    public CaseAssignmentService(CaseAssignmentRepository repository, CaseAssignmentMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseAssignmentService]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("CaseAssignment");
        createExcel(workbook, sheet, CaseAssignmentDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, CaseAssignmentDTO.class, null);
    }
}
