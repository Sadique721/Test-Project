package com.savbill.cpm.modules.linkacceptance.controller;

import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController2;
import com.savbill.cpm.modules.linkacceptance.model.LinkAcceptanceDTO;
import com.savbill.cpm.modules.linkacceptance.service.LinkAcceptanceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.LINKACCEPTANCE)
public class LinkAcceptanceController extends ExBaseAbstractController2<LinkAcceptanceDTO> {
    public LinkAcceptanceController(LinkAcceptanceService linkAcceptanceService) {
        super(linkAcceptanceService);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

}
