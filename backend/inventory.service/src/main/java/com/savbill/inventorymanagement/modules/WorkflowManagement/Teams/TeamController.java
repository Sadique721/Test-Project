package com.savbill.inventorymanagement.modules.WorkflowManagement.Teams;

import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.TEAMS)
public class TeamController extends ExBaseAbstractController<TeamsDTO> {


    public TeamController(TeamsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[TeamController]";
    }

    @Autowired
    TeamsService teamsService;

    @GetMapping("/getAllTeamBasedOnAttchedStaff")
    public GenericDataDTO getAllTeamBasedOnAttchedStaff() throws Exception {
        GenericDataDTO genericDataDTO = teamsService.getAllTeamBasedOnAttchedStaff();
        return genericDataDTO;
    }
}
