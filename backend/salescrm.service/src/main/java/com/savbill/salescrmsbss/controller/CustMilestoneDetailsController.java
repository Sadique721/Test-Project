package com.savbill.salescrmsbss.controller;

import com.savbill.salescrmsbss.entity.pojo.CustMilestoneDetailsPojo;
import com.savbill.salescrmsbss.entity.pojo.QuickInvoicePojo;
import com.savbill.salescrmsbss.service.QuickInvoiceService;
import com.savbill.salescrmsbss.utils.URLConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = URLConstant.ROOT_ENDPOINT + URLConstant.CUST_MILESTONE_DETAILS_CONTROLLER_ENDPOINT)
public class CustMilestoneDetailsController extends BaseController {
    @Autowired
    private QuickInvoiceService custMilestoneDetailsService;

    @PostMapping("/quickInvoiceCreationWithMilestones")
    public ResponseEntity<List<CustMilestoneDetailsPojo>> quickInvoiceCreationWithMilestones(@RequestBody QuickInvoicePojo quickInvoiceCreationPojo, HttpServletRequest request) throws Exception {
        List<CustMilestoneDetailsPojo> custMileStoneList = new ArrayList<>();
            custMileStoneList= custMilestoneDetailsService.saveCustomerMileStoneWithLead(quickInvoiceCreationPojo);
        return new ResponseEntity<List<CustMilestoneDetailsPojo>>(custMileStoneList, HttpStatus.ACCEPTED);
    }


    @GetMapping("/getAllMilestones/")
    public ResponseEntity<Page<CustMilestoneDetailsPojo>> getAllMilestones(@RequestParam(name = "leadId",required = false) Long leadId){

        List<CustMilestoneDetailsPojo> mileStonePojoList = custMilestoneDetailsService.getAllMilestones(leadId);
        return new ResponseEntity<Page<CustMilestoneDetailsPojo>>(new PageImpl<CustMilestoneDetailsPojo>(mileStonePojoList), HttpStatus.OK);
    }

    @GetMapping("/getMilestoneById/")
    public ResponseEntity<CustMilestoneDetailsPojo> getMilestoneById(@RequestParam(name = "id",required = false) Long id){

        CustMilestoneDetailsPojo mileStonePojo = custMilestoneDetailsService.getMilestoneById(id);
        return new ResponseEntity<CustMilestoneDetailsPojo>(mileStonePojo, HttpStatus.OK);
    }

    @PutMapping("/updateCustMilestoneDetails/")
    public ResponseEntity<CustMilestoneDetailsPojo> updateCustMilestoneDetails(@RequestBody CustMilestoneDetailsPojo custMilestonePojo){

        CustMilestoneDetailsPojo mileStonePojo = custMilestoneDetailsService.updateCustMilestoneDetails(custMilestonePojo);
        return new ResponseEntity<CustMilestoneDetailsPojo>(mileStonePojo, HttpStatus.OK);
    }

}
