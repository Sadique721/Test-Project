package com.savbill.revenuemanagement.core.controller.invoice.postpaid;


import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.ClientServiceConstant;
import com.savbill.revenuemanagement.core.constants.MenuConstants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.dto.common.ResponseObject;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.billrun.BillRunService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

import static com.savbill.revenuemanagement.core.util.ResponseUtil.apiResponse;

//@Api(value = "billing-engine-1.0 postpaid Management", description = "REST APIs related to PostpaidBilling Entity!!!!", tags = "PostpaidBilling")
@RestController
@RequestMapping(UrlConstants.BASE_API_URL+"/trialbillingprocess")
@CrossOrigin
public class PostpaidTrialBillingProcessRestController {
    private static final Logger logger = LoggerFactory.getLogger(PostpaidBillingProcessRestController.class);

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @Autowired
    private PostpaidInvoiceService postpaidInvoiceService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @PreAuthorize("validatePermission(\"" + MenuConstants.postpaid_gene_bill_run + "\" ,\"" + MenuConstants.postpaid_gene_trial_bill_run + "\")")
    @RequestMapping(value = "/generatebill/{nextbilldate}", method = RequestMethod.GET)
    public ResponseObject generatebill(@PathVariable("nextbilldate") String nextbilldate) {
        ResponseObject response = new ResponseObject();
        logger.debug("[BillingProcessHelper] billgenerate() called with " + nextbilldate + " time: " + new Date().getTime());
        response = postpaidInvoiceService.createPostpaidTrialInvoice(nextbilldate);
        logger.debug("[BillingProcessHelper] billgenerate() end " + nextbilldate + " time: " + new Date().getTime());
        return response;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.postpaid_bill_run_master + "\")")
    @PostMapping("/billrun/list")
    public ResponseEntity<?> getBillRun(@RequestBody PaginationRequestDTO requestDTO,
                                        @RequestParam(name = "type") String type) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<BillRun> billRunList = null;
        MDC.put("type", "Fetch");
        try {
            BillRunService billRunService = SpringContext.getBean(BillRunService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            billRunList = billRunService.getList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(),
                    requestDTO.getSortOrder(), requestDTO.getFilters(), type);
            response.put("billrunlist", billRunService.convertResponseModelIntoPojo(billRunList.getContent()));
            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("fetching billrun list   :  request: { From : {}}; Response : {{]}", MODULE, RESP_CODE, response);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Unable to Fetch billrun list:  request: { From : {}, }; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to Fetch billrun list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());

        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response, billRunList);
    }

//    @RequestMapping(value = {"/billrun/generatepdf"}, method = RequestMethod.GET)
//    public String generatePDFs(@RequestParam(name="bid",defaultValue="")  String billRunId, RedirectAttributes redirectAttributes, Model model) {
//
//        boolean bError=true;
//        try {
//            if(billRunId!=null) {
//                BillRunService billRunService = SpringContext.getBean(BillRunService.class);
//                boolean bStatus=billRunService.generateInvoice(billRunId);
//                if(bStatus) {
//                    bError=false;
//                }
//            }
//        }catch(Exception e) {
//            e.printStackTrace();
//            bError=true;
//        }
//        if(bError) {
//            redirectAttributes.addAttribute("flashMsgType","Error");
//            redirectAttributes.addAttribute("flashMessage","Error performing operation.Please try after sometime..");
//        }
//        return "redirect:/billrun/search";
//
//    }

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).getValue());
        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).getValue();
        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).getValue());
        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }
}
