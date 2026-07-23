package com.savbill.partnermanagement.modules.Teams;

import com.savbill.partnermanagement.core.constants.UrlConstants;
import com.savbill.partnermanagement.core.controller.ExBaseAbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PARTNER_CONTROLLER + UrlConstants.TEAMS)
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

//    @GetMapping("/getAllTeamBasedOnAttchedStaff")
//    public GenericDataDTO getAllTeamBasedOnAttchedStaff() throws Exception {
//        GenericDataDTO genericDataDTO = teamsService.getAllTeamBasedOnAttchedStaff();
//        return genericDataDTO;
//    }
}
