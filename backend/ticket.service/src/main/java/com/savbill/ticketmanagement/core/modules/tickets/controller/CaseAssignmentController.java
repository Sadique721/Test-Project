package com.savbill.ticketmanagement.core.modules.tickets.controller;


import com.savbill.ticketmanagement.core.controller.ExBaseAbstractController;
import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
import com.savbill.ticketmanagement.core.modules.tickets.model.CaseAssignmentDTO;
import com.savbill.ticketmanagement.core.modules.tickets.service.CaseAssignmentService;
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

