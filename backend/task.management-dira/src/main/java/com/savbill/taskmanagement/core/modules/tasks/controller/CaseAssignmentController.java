package com.savbill.taskmanagement.core.modules.tasks.controller;


import com.savbill.taskmanagement.core.controller.ExBaseAbstractController;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseAssignmentDTO;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseAssignmentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.CASE_ASSIGNMENT)
public class CaseAssignmentController extends ExBaseAbstractController<CaseAssignmentDTO> {

    public CaseAssignmentController(CaseAssignmentService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseAssignmentController]";
    }
}

