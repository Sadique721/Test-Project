package com.savbill.taskmanagement.core.modules.tasks.service;


import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchModel;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseAssignment;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseAssignmentMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseAssignmentDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseAssignmentRepository;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
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
